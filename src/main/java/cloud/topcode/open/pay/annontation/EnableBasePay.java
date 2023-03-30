package cloud.topcode.open.pay.annontation;

import cloud.topcode.open.pay.config.PayAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(PayAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableBasePay {
}

