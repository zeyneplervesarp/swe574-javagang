import swal from 'sweetalert2'
import statuses from './statuses'

export default {
    confirm: function (title, message, onSuccess, image, onFailure) {
        if (message) {
            swal.fire({
                title: title,
                text: message,
                type: image ? '' : 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                imageUrl: image
            }).then((result) => {
                if (result.value && result.value === true && typeof onSuccess === 'function') {
                    onSuccess()
                } else if (result.dismiss && (result.dismiss === 'cancel' || result.dismiss === 'overlay') && typeof onFailure === 'function') {
                    onFailure()
                }
            })
        }
    },
    show: function (status, onSuccess) {
        if (status.message) {
            swal.fire({
                title: statuses.isSuccess(status)
                    ? 'Tamamlandı'
                    : statuses.isError(status)
                        ? 'Hata'
                        : statuses.isWarning(status)
                            ? 'Uyarı'
                            : '',
                text: status.message,
                type: statuses.isSuccess(status)
                    ? 'success'
                    : statuses.isError(status)
                        ? 'error'
                        : statuses.isWarning(status)
                            ? 'warning'
                            : '',
                allowOutsideClick: false
            }).then(function (result) {
                if (result.value && result.value === true && typeof onSuccess === 'function') {
                    onSuccess()
                }
            })
        }
    },
    prompt: (title, placeholder, validator, onResult, isInputRequired = true) => {
        swal.fire({
            title: title || '',
            input: 'text',
            inputPlaceholder: placeholder || '',
            showCancelButton: true,
            confirmButtonText: 'Ok',
            cancelButtonText: 'Cancel',
            inputValidator: (value) => {
                if (typeof validator === 'function') {
                    return validator(value)
                } else {
                    if (console) {
                        // eslint-disable-next-line no-console
                        console.warn('"validator" is not provided!')
                    }
                }

                return true
            }
        }).then((result) => {
            if ((!isInputRequired && !result.dismiss) || (result && result.value)) {
                if (typeof onResult === 'function') {
                    onResult(result)
                } else {
                    if (console) {
                        // eslint-disable-next-line no-console
                        console.warn('"onResult" is not provided!')
                    }
                }
            }
        })
    },
    showError: function(message)
    {
        var text;
        if(message) text = message;
        else text = 'Something went wrong!'

        swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: text
          })
    },
    showSuccess: function(message)
    {
        var text;
        if(message) text = message;
        else text = ''

        swal.fire({
            icon: 'success',
            title: 'Success',
            text: text
          })
    }
}