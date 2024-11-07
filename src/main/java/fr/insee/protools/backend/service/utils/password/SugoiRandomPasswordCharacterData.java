package fr.insee.protools.backend.service.utils.password;

import org.passay.CharacterData;

/**
 * Special set of character data for password generation.
 */
public enum SugoiRandomPasswordCharacterData implements CharacterData {

    /**
     * Lower case characters without i, l and o.
     */
    LOWERCASE("INSUFFICIENT_LOWERCASE", "abcdefghjkmnpqrstuvwxyz"),

    /**
     * Upper case characters without I, O and Q.
     */
    UPPERCASE("INSUFFICIENT_UPPERCASE", "ABCDEFGHJKLMNPRSTUVWXYZ"),

    /**
     * Digit characters without 1 and 0.
     */
    DIGIT("INSUFFICIENT_DIGIT", "23456789"),

    /**
     * Alphabetical characters (upper and lower case).
     */
    ALPHABETICAL("INSUFFICIENT_ALPHABETICAL", UPPERCASE.getCharacters() + LOWERCASE.getCharacters()),

    /**
     * Special characters.
     */
    SPECIAL(
            "INSUFFICIENT_SPECIAL",
            // ASCII symbols
            "!$%&()*+?@");

    /**
     * Error code.
     */
    private final String errorCode;

    /**
     * Characters.
     */
    private final String characters;

    /**
     * Creates a new english character data.
     *
     * @param code       Error code.
     * @param charString Characters as string.
     */
    SugoiRandomPasswordCharacterData(final String code, final String charString) {
        errorCode = code;
        characters = charString;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getCharacters() {
        return characters;
    }
}
