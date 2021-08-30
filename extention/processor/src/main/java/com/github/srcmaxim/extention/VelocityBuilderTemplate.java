package com.github.srcmaxim.extention;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;

public class VelocityBuilderTemplate implements BuilderTemplate {

  private static Logger LOG = Logger.getAnonymousLogger();
  private static String BUILDER_TEMPLATE = """
      package $package;

      public class $builderClass {
        
        #foreach( $field in $fields )
        private $field.type() $field.name();
        #end
        
        #foreach( $field in $fields )
        public $builderClass $field.name()($field.type() $field.name()) {
          this.$field.name() = $field.name();
          return this;
        }#if( $foreach.hasNext )$newline#end
        #end

        private $builderClass() {}

        public static $builderClass builder() {
          return new $builderClass();
        }

        public $class build() {
          return new $class(
            #foreach( $field in $fields )
            $field.name()#if( $foreach.hasNext ),#end
            #end
          );
        }
      }
      """;

  private final Template template;

  public VelocityBuilderTemplate() {
    template = getTemplate();
  }

  @Override
  public CharSequence build(Builder builder) {
    try {
      var context = getContext(builder);
      var sw = new StringWriter();
      template.merge(context, sw);
      return sw.toString();
    } catch (ResourceNotFoundException rnfe) {
      LOG.severe("couldn't find the template");
    } catch (ParseErrorException pee) {
      LOG.severe("syntax error: problem parsing the template");
    } catch (MethodInvocationException mie) {
      LOG.severe("something invoked in the template threw an exception");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Velocity template exception: ");
      LOG.severe(e.getMessage());
    }
    System.exit(1);
    return "";
  }

  private static Template getTemplate() {
    Velocity.init();
    var runtimeServices = RuntimeSingleton.getRuntimeServices();
    var reader = new StringReader(BUILDER_TEMPLATE);
    var template = new Template();
    template.setRuntimeServices(runtimeServices);
    try {
      template.setData(runtimeServices.parse(reader, template));
    } catch (ParseException e) {
      LOG.log(Level.SEVERE, "Velocity template exception: ");
      LOG.log(Level.SEVERE, e.getMessage());
      System.exit(1);
    }
    template.initDocument();
    return template;
  }

  private static VelocityContext getContext(Builder builder) {
    Map<String, Object> context = new HashMap<>(4);
    context.put("package", builder.annotatedClassPackage());
    context.put("class", builder.annotatedClassName());
    context.put("builderClass", builder.builderClassName());
    context.put("fields", builder.fields());
    context.put("newline", '\n');
    return new VelocityContext(context);
  }

}
