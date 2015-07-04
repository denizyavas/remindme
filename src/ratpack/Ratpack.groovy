import com.deniz.ratpack.handlers.agency.NewValueHandler
import com.deniz.ratpack.handlers.customer.TrackTranslationHandler
import com.deniz.ratpack.handlers.monitoring.HealthCheckHandler
import com.deniz.ratpack.handlers.customer.TranslationRequestHandler
import com.deniz.ratpack.monitoring.MonitoringService
import com.deniz.remindme.RatpackSpringConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import ratpack.jackson.JacksonModule
import ratpack.rx.RxRatpack

import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack");

ratpack {

    HealthCheckHandler healthCheckHandler
    TranslationRequestHandler requestHandler

    bindings {
        add new JacksonModule()

        init {
            RxRatpack.initialize()

            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RatpackSpringConfig)
            ctx.beanFactory.registerSingleton "launchConfig", launchConfig
            ctx.beanFactory.registerSingleton "execControl", launchConfig.execController.control

            requestHandler = ctx.getBean TranslationRequestHandler
            newValueHandler = ctx.getBean NewValueHandler
            trackTranslationHandler = ctx.getBean TrackTranslationHandler

            healthCheckHandler = new HealthCheckHandler(monitoringService: new MonitoringService())
        }
    }

    handlers {
        get("healthcheck", healthCheckHandler)

        handler("question/:questionId") {
            byMethod {
                post(questionSaveHandler)
                get(questionReadHandler)
            }
        }

        get("questions/list", questionListHandler)
    }
}
