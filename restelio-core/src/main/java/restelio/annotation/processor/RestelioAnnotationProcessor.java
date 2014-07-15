/*
 * Copyright 2014 Matteo Giaccone and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package restelio.annotation.processor;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import restelio.annotation.RestelioFactory;
import restelio.annotation.RestelioFilter;
import restelio.annotation.RestelioResource;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;

/**
 * Base class for annotation processing
 * @author Matteo Giaccone
 */
@SupportedAnnotationTypes({
        "restelio.annotation.RestelioFactory",
        "restelio.annotation.RestelioResource",
        "restelio.annotation.RestelioFilter"
})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RestelioAnnotationProcessor extends AbstractProcessor {

    static final Logger log = LoggerFactory.getLogger(RestelioAnnotationProcessor.class);

    private static final String TPL_INITIALIZER     = "/META-INF/template/restelio_initializer.st";
    private static final String OUTPUT_CLASS_NAME   = "RestelioInitializer";
    private static final String OUTPUT_CLASS_FQN    = "generated." + OUTPUT_CLASS_NAME;

    private boolean done;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!done) {
            Set<?> factories = roundEnv.getElementsAnnotatedWith(RestelioFactory.class);
            Set<?> filters = roundEnv.getElementsAnnotatedWith(RestelioFilter.class);
            Set resources = roundEnv.getElementsAnnotatedWith(RestelioResource.class);
            //roundEnv.

            try {
                generateInitializer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            done = true;
        }

//        System.out.println("********************\n\n\n\n\n\n");
//        System.out.println("ANNOTATION PROCESSOR\n\n\n\n\n\n");
//        System.out.println("********************");
//        try {
//            JavaFileObject jfo = processingEnv.getFiler()
//                    .createSourceFile("generated.RestilioInit");
//
//            BufferedWriter bw = new BufferedWriter(jfo.openWriter());
//            bw.append("package generated;");
//            bw.newLine();
//            bw.newLine();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        resources.
//        for (fac=)
//        if (e.getKind() == ElementKind.CLASS) {
//            TypeElement classElement = (TypeElement) e;
//            PackageElement packageElement =
//                    (PackageElement) classElement.getEnclosingElement();
//
//            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
//                    classElement.getQualifiedName() + "BeanInfo");
//
//            BufferedWriter bw = new BufferedWriter(jfo.openWriter());
//            bw.append("package ");
//            bw.append(packageElement.getQualifiedName());
//            bw.append(";");
//            bw.newLine();
//            bw.newLine();
//            // rest of generated class contents
        return true;
    }

    private void generateInitializer() throws IOException {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(TPL_INITIALIZER));
        String templateSource = CharStreams.toString(reader);

        STGroup stGroup = new STGroupString(templateSource);
        ST st = stGroup.getInstanceOf("initializer");

//        st.add("translations", values);
//        st.add("creationTime", new Date().toString());

        JavaFileObject javaFile = processingEnv.getFiler().createSourceFile(OUTPUT_CLASS_FQN);
        OutputStream output = javaFile.openOutputStream();
        ByteStreams.copy(new ByteArrayInputStream(st.render().getBytes()), output);
        output.flush();
        output.close();
    }

//    private void generateOutputFromTemplate(String template, List<Translation> values, File outputFile) throws IOException {
//        String templateSource = IOUtils.toString(getClass().getResourceAsStream(template));
//
//        STGroup stGroup = new STGroupString(templateSource);
//        ST st = stGroup.getInstanceOf("resource");
//
//        st.add("translations", values);
//        st.add("creationTime", new Date().toString());
//
//        String outputContent = st.render();
//        FileUtils.write(outputFile, outputContent, Charsets.UTF_8);
//    }
}
