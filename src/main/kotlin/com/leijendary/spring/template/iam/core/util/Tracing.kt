package com.leijendary.spring.template.iam.core.util

import com.leijendary.spring.template.iam.core.util.BeanContainer.TRACER
import io.micrometer.tracing.TraceContext
import org.slf4j.MDC

const val HEADER_TRACE_PARENT = "traceparent"

private const val MDC_TRACE_ID = "traceId"
private const val MDC_SPAN_ID = "spanId"

object Tracing {
    fun get(): TraceContext = TRACER.nextSpan().context()

    fun log(traceParent: String?, function: () -> Unit) {
        traceParent
            ?.split("-")
            ?.let {
                MDC.put(MDC_TRACE_ID, it[1])
                MDC.put(MDC_SPAN_ID, it[2])
            }
            .run {
                function()
            }
            .run {
                MDC.remove(MDC_TRACE_ID)
                MDC.remove(MDC_SPAN_ID)
            }
    }
}
