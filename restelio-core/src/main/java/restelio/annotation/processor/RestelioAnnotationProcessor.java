package restelio.annotation.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.annotation.RestelioFactory;
import restelio.annotation.RestelioFilter;
import restelio.annotation.RestelioResource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Base class for annotation processing
 * @author Matteo Giaccone
 */
//@SupportedAnnotationTypes({
//        "restelio.annotation.RestelioFactory",
//        "restelio.annotation.RestelioFilter",
//        "restelio.annotation.RestelioResource"
//})
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RestelioAnnotationProcessor extends AbstractProcessor {

    static final Logger log = LoggerFactory.getLogger(RestelioAnnotationProcessor.class);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log.info("Started annotation processing!!!");
        Set<?> factories = roundEnv.getElementsAnnotatedWith(RestelioFactory.class);
        Set<?> filters = roundEnv.getElementsAnnotatedWith(RestelioFilter.class);
        Set<?> resources = roundEnv.getElementsAnnotatedWith(RestelioResource.class);
        return true;
    }

}
