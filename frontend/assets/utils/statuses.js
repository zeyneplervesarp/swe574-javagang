export default {
    Success: 1,
    Warning: 2,
    Error: 4,
    /**
     *
     * @param {*} code
     */
    isSuccess (input) {
        return input && input.statusCode === 1
    },
    /**
     *
     * @param {*} code
     */
    isWarning (input) {
        return input && input.statusCode === 2
    },
    /**
     *
     * @param {*} code
     */
    isError (input) {
        return input && input.statusCode === 4
    }
}