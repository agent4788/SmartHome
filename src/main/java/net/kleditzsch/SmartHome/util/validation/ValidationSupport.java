package net.kleditzsch.SmartHome.util.validation;

import net.kleditzsch.SmartHome.util.validation.Interface.Validator;
import net.kleditzsch.SmartHome.util.validation.Validator.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Validiert ein Annotiertes Objekt
 */
public class ValidationSupport {

    /**
     * Sprachpaket
     */
    private static ResourceBundle resourceBundle;

    /**
     * Liste der Validatoren
     */
    private static Set<Validator> validators = new HashSet<Validator>();

    /**
     * Liste aller validierungs Fehler
     */
    private List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    /**
     * lädt die Validatoren
     */
    private void loadValidators() {

        if(validators.size() == 0) {

            validators.add(new MinValidator());
            validators.add(new MaxValidator());
            validators.add(new NotNullValidator());
            validators.add(new NullValidator());
            validators.add(new NotEmptyValidator());
            validators.add(new EmptyValidator());
            validators.add(new LengthValidator());
            validators.add(new SizeValidator());
            validators.add(new PatternValidator());
            validators.add(new PastValidator());
            validators.add(new FutureValidator());
            validators.add(new IpAddressValidator());
        }
    }

    /**
     * validiert das Objekt
     *
     * @param object Objekt
     * @return valide
     */
    public boolean validate(Object object) {

        loadValidators();
        validationErrors.clear();
        boolean valid = true;
        try {

            Class clazz = object.getClass();
            while(clazz != null) {

                Field[] fields = clazz.getDeclaredFields();
                Optional<ResourceBundle> resourceBundle = getResourceBundle();
                for(Field field : fields) {

                    //alle Validatoren auf jedes Feld Anwenden
                    for (Validator validator : validators) {

                        if(!validator.validate(object, field, resourceBundle)) {

                            //Validierung fehlgeschlagen
                            valid = false;
                            Optional<ValidationError> validationErrorOptional = validator.getValidationError();
                            if(validationErrorOptional.isPresent()) {

                                validationErrors.add(validationErrorOptional.get());
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return valid;
        } catch (IllegalAccessException e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * gibt die Liste mit allen Validierungsfehlern zurück
     *
     * @return Liste mit allen Validierungsfehlern
     */
    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    /**
     * gibt das Sprachpaket zurück
     *
     * @return Sprachpaket
     */
    public static Optional<ResourceBundle> getResourceBundle() {
        return Optional.ofNullable(resourceBundle);
    }

    /**
     * setzt das Sprachpaket
     *
     * @param resourceBundle Sprachpaket
     */
    public static void setResourceBundle(ResourceBundle resourceBundle) {
        ValidationSupport.resourceBundle = resourceBundle;
    }
}
