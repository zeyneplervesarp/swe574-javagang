/* eslint-disable no-debugger */
import axios from 'axios'
import statuses from '../utils/statuses'
import modal from '../utils/modal'


const handleError = (e, message) => {
   if (e) {   
        try {
            let msg = e.response.data.message;
            if (msg) {
                modal.showError(msg);
            }
        } catch (error) {
            if(message) e.message = message;     
            modal.showError(e.message)   
        }
    } else if (e && e.message && e.message.indexOf('timeout') !== -1) {
        modal.show(e)
    }

}

const handleSuccess = (e, type) => {
    if (e && e.data) {
        if (type) {
            modal.showSuccess(type)

        }

        let message = ''
        if (message.length > 0) {
            if (type === undefined || type === 'modal') {
                modal.show({ StatusCode: 2, message: message })
            } else {
                modal.show({ StatusCode: 2, message: message })
            }
        }
    }

    return true
}

const getHeaders = () => {
    let token = JSON.parse(localStorage.getItem('token'));

    if (token) {
        return { Authorization: 'Bearer ' + token };
    } else {
        return {};
    }
}

export default {

    delete(url, data, handleOnError,errorMessage,successMessage) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'DELETE',
                url: url,
                data: data,
                headers: getHeaders(),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleSuccess(r, successMessage)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === true) {
                    handleError(e, errorMessage)
                }
                else {
                    reject(e)
                }
            })
        })
    },
    post(url, data, handleOnError,errorMessage, successMessage) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'POST',
                url: url,
                data: data,
                headers: getHeaders(),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleSuccess(r, successMessage)) {
                    resolve(r.data)
                }
            }).catch(function(e)  {
                if (handleOnError === true) {
                    handleError(e,errorMessage)
                }
                else {
                    reject(e)
                }
            })
        })
    },
    put(url, data, handleOnError, errorMessage,successMessage) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'PUT',
                url: url,
                data: data,
                headers: getHeaders(),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleSuccess(r, successMessage)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === true) {
                    handleError(e, errorMessage)
                }
                else {
                    reject(e)
                }
            })
        })
    },
    get(url, data, handleOnError, errorMessage, successMessage) {
        return new Promise((resolve, reject) => {
            let queryString = ''
            if (data) {
                queryString = Object.keys(data).reduce(function (str, key, i) {
                    let delimiter, val
                    delimiter = (i === 0) ? '?' : '&'
                    key = encodeURIComponent(key)
                    val = encodeURIComponent(data[key])
                    return [str, delimiter, key, '=', val].join('')
                }, '')
            }
            var headers = getHeaders(headers);
            axios({
                method: 'GET',
                url: url + queryString,
                headers: headers,
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleSuccess(r, successMessage)) {
                    resolve(r.data)
                }
            }).catch(function(e) {
                if (handleOnError === true) {
                    handleError(e,errorMessage)
                }
                else {
                    reject(e)
                }
            })
        })
    }
}
