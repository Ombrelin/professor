package fr.arsenelapostolet.professor.viewmodels.utils

class ViewModelProperty<T>(initialValue: T) {
    private var _value = initialValue

    var value: T
        get() = _value
        set(value) {
            val oldValue = this.value
            if (oldValue != value) {
                _value = value
                onChange?.invoke(oldValue, value)
            }

        }

    private var onChange: ((oldValue: T, newValue: T) -> Unit)? = null

    fun registerHandler(onChange: (oldValue: T, newValue: T) -> Unit) {
        this.onChange = onChange
    }

    override fun toString(): String = "ObservableProperty(value=$value)"
}