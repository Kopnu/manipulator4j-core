package love.korni.manipulator.localization;

import lombok.Data;

import java.util.Locale;

/**
 * Settings for {@link DefaultLocalization}.
 *
 * @author Sergei_Kornilov
 */
@Data
public class LocalizationSettings {
    private String[] basenames;
    private Locale defaultLocale;
}
