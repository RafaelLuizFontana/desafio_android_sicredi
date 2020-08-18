package br.com.sicredi.eventos

interface ForegroundInterface<R> {
    fun preStartBackgroundExecute()
    fun onSuccessBackgroundExecute(result: R?)
    fun onFailureBackgroundExecute(throwable: Throwable?)
    fun onFinishBackgroundExecute()
}