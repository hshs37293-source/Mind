package dev.mind.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.GpuDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Wykrywanie aktywnego backendu graficznego (Vulkan / OpenGL) w Minecrafcie 26.3.
 *
 * <p>NIEPRZETESTOWANE: klasa zależy od Minecrafta i nie była kompilowana ani uruchamiana.
 * Nazwy klas i metod pochodzą z kodu Render4U 1.3.2 (pakiet {@code com.mojang.renderpearl.api.device},
 * {@code RenderSystem#getDevice}, {@code GpuDevice#getDeviceInfo().backendName()}).
 *
 * <p>Ulepszenia względem {@code VulkanHelper} z Render4U: wynik jest enumem, a nie tekstem,
 * błąd odczytu jest logowany (raz), a cache można unieważnić przez {@link #invalidate()}
 * (np. po przeładowaniu zasobów).
 */
public final class GraphicsBackend {
    public enum Backend { VULKAN, OPENGL, UNKNOWN }

    private static final Logger LOGGER = LoggerFactory.getLogger("Mind/GraphicsBackend");

    private static volatile Backend cached;
    private static boolean warned;

    private GraphicsBackend() {
    }

    public static Backend current() {
        Backend value = cached;
        if (value != null) {
            return value;
        }
        value = detect();
        // UNKNOWN nie jest zapamiętywane, bo urządzenie może jeszcze nie być gotowe.
        if (value != Backend.UNKNOWN) {
            cached = value;
        }
        return value;
    }

    public static boolean isVulkan() {
        return current() == Backend.VULKAN;
    }

    public static void invalidate() {
        cached = null;
    }

    private static Backend detect() {
        try {
            GpuDevice device = RenderSystem.getDevice();
            if (device == null || device.getDeviceInfo() == null) {
                return Backend.UNKNOWN;
            }
            String name = device.getDeviceInfo().backendName();
            if (name == null) {
                return Backend.UNKNOWN;
            }
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "vulkan" -> Backend.VULKAN;
                case "opengl" -> Backend.OPENGL;
                default -> Backend.UNKNOWN;
            };
        } catch (RuntimeException e) {
            if (!warned) {
                warned = true;
                LOGGER.warn("Nie udało się odczytać backendu graficznego", e);
            }
            return Backend.UNKNOWN;
        }
    }
}
