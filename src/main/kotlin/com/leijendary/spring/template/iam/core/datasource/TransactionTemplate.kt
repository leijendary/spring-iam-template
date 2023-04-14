package com.leijendary.spring.template.iam.core.datasource

import com.leijendary.spring.template.iam.core.util.BeanContainer.TRANSACTION_MANAGER
import org.springframework.transaction.support.TransactionTemplate

/**
 * Use this function if you don't want your whole method to run under a single transaction.
 * This is faster as there could be other functions/methods running under the same
 * transaction but does not necessarily need to run in a transaction.
 */
fun <T> transactional(readOnly: Boolean = false, function: () -> T?): T? {
    val template = TransactionTemplate(TRANSACTION_MANAGER).apply {
        isReadOnly = readOnly
    }

    return template.execute { function() }
}
