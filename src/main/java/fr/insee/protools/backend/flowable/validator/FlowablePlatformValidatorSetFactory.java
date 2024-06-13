
package fr.insee.protools.backend.flowable.validator;
 
 import fr.insee.protools.backend.flowable.validator.impl.PlatformServiceTasksValidator;
import org.flowable.validation.validator.Validator;
 import org.flowable.validation.validator.ValidatorSet;

 public class FlowablePlatformValidatorSetFactory
 {
   public ValidatorSet createFlowablePlatformValidatorSet() {
     ValidatorSet validatorSet = new ValidatorSet("flowable-platform");
     validatorSet.addValidator((Validator)createPlatformServiceTasksValidator());
     return validatorSet;
   }
   
   protected PlatformServiceTasksValidator createPlatformServiceTasksValidator() {
     PlatformServiceTasksValidator validator = new PlatformServiceTasksValidator();
     
     return validator;
   }
 }