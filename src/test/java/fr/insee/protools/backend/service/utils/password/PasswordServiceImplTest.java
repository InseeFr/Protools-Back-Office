package fr.insee.protools.backend.service.utils.password;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {
        "fr.insee.protools.password.create.withDigits = true",
        "fr.insee.protools.password.create.withUpperCase = true",
        "fr.insee.protools.password.create.withLowerCase = true",
        "fr.insee.protools.password.create.withSpecial = true",
})
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = PasswordServiceImpl.class) //For autowire
//These 2 are needed to inject @Values
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
class PasswordServiceImplTest {

    @Autowired
    private PasswordServiceImpl passwordService;


    @Test
    void testGeneratePassword() {
        //Prepare
        var rules = ((PasswordServiceImpl) passwordService).generateRandomPasswordCharacterRules(true, true, true, true);
        PasswordValidator passwordValidator = new PasswordValidator(rules);

        //Generate and validate X1000
        for (int i = 0; i < 1000; i++) {
            for (int length = 8; length < 19; length++) { // and for various sizes
                //Execute method under test
                String generatePassword = passwordService.generatePassword(length);

                //Post condition
                PasswordData passwordData = new PasswordData(generatePassword);
                RuleResult validate = passwordValidator.validate(passwordData);

                assertTrue(validate.isValid(), "Generated password meets the rules : " + generatePassword);
                assertEquals(length, generatePassword.length(), "Generated password length should meets the confif : " + generatePassword);
            }
        }

    }
}