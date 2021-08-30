package com.github.srcmaxim.extention;

import static java.util.stream.Collectors.toList;

import com.github.srcmaxim.extention.BuilderTemplate.Builder;
import com.github.srcmaxim.extention.BuilderTemplate.Field;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion(SourceVersion.RELEASE_16)
@SupportedAnnotationTypes("com.github.srcmaxim.extention.Builder")
public class BuilderProcessor extends AbstractProcessor {

  private static Logger LOG = Logger.getAnonymousLogger();

  private final BuilderTemplate builderTemplate = new VelocityBuilderTemplate();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    annotations.stream()
        .map(roundEnv::getElementsAnnotatedWith)
        .mapMulti(Iterable::forEach)
        .map(Element.class::cast)
        .forEach(this::processAnnotatedClass);
    return true;
  }

  private void processAnnotatedClass(Element element) {
    var annotatedClassPackage = element.getEnclosingElement().toString();
    var annotatedClassName = element.getSimpleName().toString();
    var builderClassName = annotatedClassName + "Builder";

    var builderFields = getBuilderFields(element);
    var builder = new Builder(annotatedClassPackage, annotatedClassName, builderClassName, builderFields);
    var builderClassCode = builderTemplate.build(builder);
    var builderFullQualifiedClassName = String.join(".", annotatedClassPackage, builderClassName);

    try {
      var builderFile = processingEnv.getFiler()
          .createSourceFile(builderFullQualifiedClassName, element.getEnclosingElement());
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.println(builderClassCode);
      }
    } catch (IOException e) {
      LOG.severe(e.getMessage());
    }
  }

  private List<Field> getBuilderFields(Element element) {
    return element.getEnclosedElements()
        .stream()
        .filter(e -> e.getKind() == ElementKind.FIELD)
        .map(f -> new Field(f.asType().toString(), f.getSimpleName().toString()))
        .collect(toList());
  }

}
