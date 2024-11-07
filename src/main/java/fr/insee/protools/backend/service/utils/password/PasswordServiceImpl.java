package fr.insee.protools.backend.service.utils.password;

import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Value("${fr.insee.protools.password.create.withDigits:true}")
    private boolean pCreateWithDigits;

    @Value("${fr.insee.protools.password.create.withUpperCase:true}")
    private boolean pCreateWithUpperCase;

    @Value("${fr.insee.protools.password.create.withLowerCase:true}")
    private boolean pCreateWithLowerCase;

    @Value("${fr.insee.protools.password.create.withSpecial:true}")
    private boolean pCreateWithSpecial;

    public static List<CharacterRule> generateRandomPasswordCharacterRules(
            Boolean withUpperCase, Boolean withLowerCase, Boolean withDigit, Boolean withSpecial) {
        List<CharacterRule> characterRules = new ArrayList<>();
        if (Boolean.TRUE.equals(withUpperCase))
            characterRules.add(new CharacterRule(SugoiRandomPasswordCharacterData.UPPERCASE, 1));
        if (Boolean.TRUE.equals(withLowerCase))
            characterRules.add(new CharacterRule(SugoiRandomPasswordCharacterData.LOWERCASE, 1));
        if (Boolean.TRUE.equals(withDigit))
            characterRules.add(new CharacterRule(SugoiRandomPasswordCharacterData.DIGIT, 1));
        if (Boolean.TRUE.equals(withSpecial))
            characterRules.add(new CharacterRule(SugoiRandomPasswordCharacterData.SPECIAL, 1));
        return characterRules;
    }

    @Override
    public String generatePassword(int pCreateSize) {

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        return passwordGenerator.generatePassword(
                pCreateSize,
                generateRandomPasswordCharacterRules(
                        pCreateWithUpperCase,
                        pCreateWithLowerCase,
                        pCreateWithDigits,
                        pCreateWithSpecial));
    }

}
