package by.andd3dfx.templateapp.persistence;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Custom FunctionContributor supporting some specific SQL functions
 * <p>
 * It should be registered in `src/main/resources/META-INF/services/org.hibernate.boot.model.FunctionContributor` file
 */
public class CustomFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        var functionRegistry = functionContributions.getFunctionRegistry();
        var basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();

        // Function for checking only country
        functionRegistry.registerPattern("sameCountry", "?1 @> jsonb_build_object('country', ?2)",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));

        // Function for checking only city
        functionRegistry.registerPattern("sameCity", "?1 @> jsonb_build_object('city', ?2)",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));

        // Function for checking both country and city
        functionRegistry.registerPattern("sameLocation", "?1 @> jsonb_build_object('country', ?2, 'city', ?3)",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
    }
}
