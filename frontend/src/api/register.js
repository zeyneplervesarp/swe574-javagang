/* eslint-disable no-debugger */
import http from '../utils/http'

// url, data, handleOnError,successMessage
export default {
    Register(data) {
        return http.post(process.env.VUE_APP_API + 'register', data, true, "Please enter valid data!")
    },
    Login(data) {
        return http.post(process.env.VUE_APP_API + 'login', data, true, "Please enter valid data!")
    },
    GetTags() {
        return http.get(process.env.VUE_APP_API + 'tags')
    },
    GetProfile(id) {
        if (id == null)
            return http.get(process.env.VUE_APP_API + 'user', null, true)
        else
            return http.get(process.env.VUE_APP_API + 'user/' + id, null, true)

    },
    CreateService(data) {
        return http.post(process.env.VUE_APP_API + 'service', data, true, null, "Create Service Successful")
    },
    GetService(id) {
        return http.get(process.env.VUE_APP_API + 'service/' + id, null, true)
    },
    GetServicesByUser() {
        return http.get(process.env.VUE_APP_API + 'service/userService')
    },
    GetAllServices(getOngoingOnly, filter) {
        return http.get(process.env.VUE_APP_API + 'service/' + getOngoingOnly + '/' + filter)
    },
    GetAllServicesSorted(getOngoingOnly, filter, sortBy) {
        return http.get(process.env.VUE_APP_API + 'service/' + getOngoingOnly + '/' + filter + "?sortBy=" + sortBy)
    },
    GetFeaturedServices() {
        return http.get(process.env.VUE_APP_API + 'service/feature')
    },
    FeatureService(id) {
        return http.post(process.env.VUE_APP_API + 'service/feature/' + id, null,true)
    },
    UnfeatureService(id) {
        return http.post(process.env.VUE_APP_API + 'service/unfeature/' + id, null,true)
    },
    SetTags(data) {
        return http.post(process.env.VUE_APP_API + 'user/setTags', data)
    },
    GetUserServiceDetails(serviceId) {
        return http.get(process.env.VUE_APP_API + 'user/getServiceDetails/' + serviceId)
    },
    SendUserServiceApproval(serviceId) {
        return http.get(process.env.VUE_APP_API + 'approval/request/' + serviceId, null, true, null, "Request Sent")
    },
    GetApprovalListByUser() {
        return http.get(process.env.VUE_APP_API + 'approval/userRequests', null, true)
    },
    ApproveRequest(data) {
        return http.post(process.env.VUE_APP_API + 'approval/approve', data, true, null, 'Request Approved Successfully')
    },
    DenyRequest(data) {
        return http.post(process.env.VUE_APP_API + 'approval/deny', data, true, 'Request Denied Successfully')
    },
    SendServiceOverApprovalForCreator(serviceId) {
        return http.get(process.env.VUE_APP_API + 'service/approve/' + serviceId, true, 'A notification will be sent to all attendees. Thank you!')
    },
    SendServiceOverApprovalForAttendee(serviceId) {
        return http.get(process.env.VUE_APP_API + 'service/complete/' + serviceId, true, 'Service is now complete. Thank you!')
    },
    GetNotificationDetails() {
        return http.get(process.env.VUE_APP_API + 'notification/getByUser')
    },
    ReadAllNotifications() {
        return http.get(process.env.VUE_APP_API + 'notification/readAllByUser')
    },

    FollowUser(data) {
        return http.get(process.env.VUE_APP_API + 'user/follow/' + data, null, true, null, "Successfully followed user.")
    },
    CheckIfFollowExists(data) {
        return http.get(process.env.VUE_APP_API + 'user/follow/control/' + data)
    },
    GetAllServicesForHome() {
        return http.get(process.env.VUE_APP_API + 'service')
    },
    GetTagInfo(tag) {
        return http.get(process.env.VUE_APP_API + 'tags/info/' + tag)
    },
    AddTag(tag) {
        return http.post(process.env.VUE_APP_API + 'tags', tag, false)
    },
    RateService(serviceId, rating) {
        return http.post(process.env.VUE_APP_API + 'rating/service/' + serviceId + "/" + rating, null, true)
    },    
    AddTag(tag)
    {
        return http.post(process.env.VUE_APP_API + 'tags', tag,false)
    },
    FlagService(serviceId)
    {
        return http.post(process.env.VUE_APP_API + 'service/flag/' + serviceId, null, true)
    },
    FlagUser(userId) 
    {
        return http.post(process.env.VUE_APP_API + "user/flag/" + userId, null, true)
    },
    DismissFlagsForUser(userId)
    {
        return http.post(process.env.VUE_APP_API + "user/flag/dismiss/" + userId, null, true)
    },
    DismissFlagsForService(serviceId) {
        return http.post(process.env.VUE_APP_API + "service/flag/dismiss/" + serviceId, null, true)
    },
    Search(searchQuery){
        return http.get(process.env.VUE_APP_API + 'search?query=' + searchQuery + '&limit=50',null,true,"Search could not be completed")
    }
}