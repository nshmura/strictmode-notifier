package io.github.juanimoli.strictmode.notifier.commons

class NotifierConfig private constructor() {
    val customActions: MutableList<CustomAction> = ArrayList()
    var ignoreAction: IgnoreAction? = null
    var isDebugMode = false
    var isHeadUpEnabled = true

    fun addCustomAction(customAction: CustomAction): NotifierConfig {
        customActions.add(customAction)
        return this
    }

    @JvmName("getCustomActions1")
    fun getCustomActions(): List<CustomAction> {
        return customActions
    }

    fun setIgnoreAction(ignoreAction: IgnoreAction?): NotifierConfig {
        this.ignoreAction = ignoreAction
        return this
    }

    fun setDebugMode(debugMode: Boolean): NotifierConfig {
        isDebugMode = debugMode
        return this
    }

    fun setHeadupEnabled(headupEnabled: Boolean): NotifierConfig {
        isHeadUpEnabled = headupEnabled
        return this
    }

    companion object {
        var instance: NotifierConfig = NotifierConfig()
    }
}
