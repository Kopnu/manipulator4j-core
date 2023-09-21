package love.korni.manipulator.message;

import lombok.Data;

import java.util.Locale;

/**
 * Settings for {@link DefaultMessageManager}.
 *
 * @author Sergei_Kornilov
 */
@Data
public class PropertySettings {
    /**
     * Paths to property files
     */
    private String[] basenames;
    /**
     * Locale of property bundle
     */
    private Locale defaultLocale;
}
