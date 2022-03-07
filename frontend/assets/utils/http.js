/* eslint-disable no-debugger */
import axios from 'axios'
import statuses from '../utils/statuses'
import modal from '../utils/modal'

const getHeaders = (headers) => {
    const defaultHeaders = {}
    return Object.assign(headers || {}, defaultHeaders)
}

const handleError = (e, errorType) => {
    if (e && e.response && e.response.data) {
        if (statuses.isWarning(e.response.data) || statuses.isError(e.response.data)) {
            if (errorType === undefined || errorType === 'modal') {
                modal.show(e.response.data)
            } else {
                modal.show(e.response.data)
            }
        }
    } else if (e && e.message && e.message.indexOf('timeout') !== -1) {
        modal.show(e)
    }

}

const handleStatusCode = (e, type) => {
    if (e && e.data) {
        let message = ''

        if (message.length > 0) {
            if (type === undefined || type === 'modal') {
                modal.show({StatusCode: 2, Message: message})
            } else {
                modal.show({StatusCode: 2, Message: message})
            }
        }
    }

    return true
}

export default {
    delete (url, data, headers, rejectOnError, handleOnError, messageType) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'DELETE',
                url: url,
                data: data,
                headers: getHeaders(headers),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleStatusCode(r, messageType)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === undefined || handleOnError === true) {
                    handleError(e, messageType)
                }
                if (rejectOnError === undefined || rejectOnError === true) {
                    reject(e)
                }
            })
        })
    },
    post (url, data, headers, rejectOnError, handleOnError, messageType) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'POST',
                url: url,
                data: data,
                headers: getHeaders(headers),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleStatusCode(r, messageType)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === undefined || handleOnError === true) {
                    handleError(e, messageType)
                }
                if (rejectOnError === undefined || rejectOnError === true) {
                    reject(e)
                }
            })
        })
    },
    put (url, data, headers, rejectOnError, handleOnError, messageType) {
        return new Promise((resolve, reject) => {
            axios({
                method: 'PUT',
                url: url,
                data: data,
                headers: getHeaders(headers),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleStatusCode(r, messageType)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === undefined || handleOnError === true) {
                    handleError(e, messageType)
                }
                if (rejectOnError === undefined || rejectOnError === true) {
                    reject(e)
                }
            })
        })
    },
    get (url, data, headers, rejectOnError, handleOnError, messageType) {
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

            axios({
                method: 'GET',
                url: url + queryString,
                headers: getHeaders(headers),
                timeout: 300 * 240 * 1000
            }).then((r) => {
                if (handleStatusCode(r, messageType)) {
                    resolve(r.data)
                }
            }).catch((e) => {
                if (handleOnError === undefined || handleOnError === true) {
                    handleError(e, messageType)
                }
                if (rejectOnError === undefined || rejectOnError === true) {
                    reject(e)
                }
            })
        })
    }
}
