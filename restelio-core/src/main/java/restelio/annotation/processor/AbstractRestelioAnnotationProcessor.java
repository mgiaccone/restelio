package restelio.annotation.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Base class for annotation processing
 * @author Matteo Giaccone
 */
//@SupportedAnnotationTypes("sdc.assets.annotations.Complexity")
//@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AbstractRestelioAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

}
