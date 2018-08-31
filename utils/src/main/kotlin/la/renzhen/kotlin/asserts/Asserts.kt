package la.renzhen.kotlin.asserts

import org.slf4j.LoggerFactory

enum class Errors(val status: Int, val error: String, val message: String) {
    /**请求错误*/
    BadRequest(400, "BadRequest", "Bad Request Parameter."),
    /**未认证用户*/
    Unauthorized(401, "Unauthorized", "The request requires user authentication."),
    /**权限不足*/
    Forbidden(403, "Forbidden", "The resources is forbidden to visit."),
    /**未发现*/
    NotFound(404, "NotFound", "The specified resource does not exist."),

    /**非法参数*/
    InvalidArgument(400, "InvalidArgument", "One of your provided argument is malformed or otherwise invalid."),
    /**方法不允许*/
    MethodNotAllowed(405, "MethodNotAllowed", "The specified method is not allowed against this resource."),
    /**参数缺失*/
    MissingParameter(400, "MissingParameter", "The request you input is missing some required parameters."),
    /**执行失败*/
    ExecuteFailed(417, "ExecuteFailed", "Failed to execute"),

    /**服务运行异常*/
    InternalSystemError(500, "InternalError", "We encountered an internal error. Please try again.");
}

object Asserts {

    class Error(val error: String, val message: String)

    val log = LoggerFactory.getLogger(Asserts::class.java)

    fun badRequest(check: Boolean, logs: String? = null) {
        badRequest(check, Errors.BadRequest.error, Errors.BadRequest.message, logs)
    }

    fun badRequest(check: Boolean, error: Error, logs: String? = null) {
        badRequest(check, error.error, error.message, logs = logs)
    }

    fun badRequest(check: Boolean, error: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("$logs error: error=$error, message=$message")
            }
            throw AssertException(status = Errors.BadRequest.status, error = error, message = message)
        }
    }

    fun unauthorized(check: Boolean, logs: String? = null) {
        unauthorized(check, Errors.Unauthorized.error, Errors.Unauthorized.message, logs)
    }

    fun unauthorized(check: Boolean, error: Error, logs: String? = null) {
        unauthorized(check, error.error, error.message, logs)
    }

    fun unauthorized(check: Boolean, error: String = Errors.Unauthorized.error, message: String = Errors.Unauthorized.message, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.Unauthorized, error, message)
            }
            throw AssertException(Errors.Unauthorized.status, error, message)
        }
    }

    fun forbidden(check: Boolean, logs: String? = null) {
        forbidden(check, Errors.Forbidden.error, Errors.Forbidden.message, logs)
    }

    fun forbidden(check: Boolean, error: Error, logs: String? = null) {
        forbidden(check, error.error, error.message, logs)
    }

    fun forbidden(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.Forbidden, code, message)
            }
            throw AssertException(Errors.Forbidden.status, code, message)
        }
    }

    fun notFound(check: Boolean, logs: String? = null) {
        notFound(check, Errors.NotFound.error, Errors.NotFound.message, logs)
    }

    fun notFound(check: Boolean, error: Error, logs: String? = null) {
        notFound(check, error.error, error.message, logs)
    }

    fun notFound(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.NotFound, code, message)
            }
            throw AssertException(Errors.NotFound.status, code, message)
        }
    }


    fun invalidArgument(check: Boolean, logs: String? = null) {
        invalidArgument(check, Errors.InvalidArgument.error, Errors.InvalidArgument.message, logs)
    }

    fun invalidArgument(check: Boolean, error: Error, logs: String? = null) {
        invalidArgument(check, error.error, error.message, logs)
    }

    fun invalidArgument(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.InvalidArgument, code, message)
            }
            throw AssertException(Errors.InvalidArgument.status, code, message)
        }
    }


    fun methodNotAllowed(check: Boolean, logs: String? = null) {
        methodNotAllowed(check, Errors.MethodNotAllowed.error, Errors.MethodNotAllowed.message, logs)
    }

    fun methodNotAllowed(check: Boolean, error: Error, logs: String? = null) {
        methodNotAllowed(check, error.error, error.message, logs)
    }

    fun methodNotAllowed(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.MethodNotAllowed, code, message)
            }
            throw AssertException(Errors.MethodNotAllowed.status, code, message)
        }
    }


    fun missingParameter(check: Boolean, logs: String? = null) {
        missingParameter(check, Errors.MissingParameter.error, Errors.MissingParameter.message, logs)
    }

    fun missingParameter(check: Boolean, error: Error, logs: String? = null) {
        missingParameter(check, error.error, error.message, logs)
    }

    fun missingParameter(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.MissingParameter, code, message)
            }
            throw AssertException(Errors.MissingParameter.status, code, message)
        }
    }

    fun executeFailed(check: Boolean, logs: String? = null) {
        executeFailed(check, Errors.ExecuteFailed.error, Errors.ExecuteFailed.message, logs)
    }

    fun executeFailed(check: Boolean, error: Error, logs: String? = null) {
        executeFailed(check, error.error, error.message, logs)
    }

    fun executeFailed(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.ExecuteFailed, code, message)
            }
            throw AssertException(Errors.ExecuteFailed.status, code, message)
        }
    }

    fun internalSystemError(check: Boolean, logs: String? = null) {
        internalSystemError(check, Errors.InternalSystemError.error, Errors.InternalSystemError.message, logs)
    }

    fun internalSystemError(check: Boolean, error: Error, logs: String? = null) {
        internalSystemError(check, error.error, error.message, logs)
    }

    fun internalSystemError(check: Boolean, code: String, message: String, logs: String? = null) {
        if (!check) {
            if (logs != null) {
                log.info("{} error. {} {} {}", logs, Errors.InternalSystemError, code, message)
            }
            throw AssertException(Errors.InternalSystemError.status, code, message)
        }
    }
}



