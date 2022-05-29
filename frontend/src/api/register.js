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
    CreateService(data, successMessage) {
        return http.post(process.env.VUE_APP_API + 'service', data, true, null, successMessage)
    },
    GetService(id) {
        return http.get(process.env.VUE_APP_API + 'service/' + id, null, true)
    },
    GetServicesByUser() {
        return http.get(process.env.VUE_APP_API + 'service/userService')
    },
    GetAllServices(getOngoingOnly, filter) {
        return http.get(process.env.VUE_APP_API + 'service/' + getOngoingOnly + '/' + filter +  "?size=18" )
    },
    GetRecommendedServices() {
        return http.get(process.env.VUE_APP_API + "getRecommendedServices", null, true, "Couldn't fetch recommended services");
    },
    GetAllServicesSorted(getOngoingOnly, filter, sortBy, url, size) {
        const urlToCall = url ? process.env.VUE_APP_API + url : process.env.VUE_APP_API + 'service/' + getOngoingOnly + '/' + filter + "?size=" + size + "&sortBy=" + sortBy ;
        console.log("predefined url: ", url)
        console.log("calling url", urlToCall);
        if (!url) {
            return http.get(urlToCall)
        } else {

            return http.get(urlToCall)
        }
    },
    GetAllServicesSortedWithLocation(getOngoingOnly, filter, sortBy, url, size, lat, lon){
        debugger;
        const urlToCall = url ? process.env.VUE_APP_API + url : process.env.VUE_APP_API + 'service/' + getOngoingOnly + '/' + filter + "?size=" + size + "&sortBy=" + sortBy + "&lat="+lat+"&lon="+lon;
        if (!url) {
            return http.get(urlToCall)
        } else {

            return http.get(urlToCall)
        }
    },
    GetFeaturedServices() {
        return http.get(process.env.VUE_APP_API + 'service/feature')
    },
    FeatureService(id) {
        return http.post(process.env.VUE_APP_API + 'service/feature/' + id, null, true)
    },
    UnfeatureService(id) {
        return http.delete(process.env.VUE_APP_API + 'service/feature/' + id, null, true)
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
    AddTag(tag) {
        return http.post(process.env.VUE_APP_API + 'tags', tag, false)
    },
    FlagService(serviceId) {
        return http.post(process.env.VUE_APP_API + 'service/flag/' + serviceId, null, true)
    },
    FlagUser(userId) {
        return http.post(process.env.VUE_APP_API + "user/flag/" + userId, null, true)
    },
    DismissFlagsForUser(userId) {
        return http.post(process.env.VUE_APP_API + "user/flag/dismiss/" + userId, null, true)
    },
    GetAllFlaggedUsers() {
        return http.get(process.env.VUE_APP_API + "user/flag", null, false)
    },
    DismissFlagsForService(serviceId) {
        return http.post(process.env.VUE_APP_API + "service/flag/dismiss/" + serviceId, null, true)
    },
    GetAllFlaggedServices() {
        return http.get(process.env.VUE_APP_API + "service/flag/", null, false)
    },
    GetAllStats() {
        return http.get(process.env.VUE_APP_API + 'stats', null, true, "Couldn't fetch stats");
    },

    GetDailyStats(url) {
        return http.get(process.env.VUE_APP_API + url, null, true, "Couldn't fetch daily stats");
    },
    Search(searchQuery) {
        return http.get(process.env.VUE_APP_API + 'search?query=' + searchQuery + '&limit=50', null, true, "Search could not be completed")
    },
    DeleteUser(userId) {
        return http.delete(process.env.VUE_APP_API + "user/delete/" + userId, null, true)
    },
    DeleteService(serviceId) {
        return http.delete(process.env.VUE_APP_API + "service/delete/" + serviceId, null, true)
    },
    GetAdminFeed(url, filter_key) {
        if (filter_key == "" || filter_key == null) {
            var data = null;
        } else {
            var data = { "filterKey": filter_key };
        }
        if (url == null || url == "") {
            return http.get(process.env.VUE_APP_API + 'admin/feed', data, null, true);
        }
        else {
            return http.get(process.env.VUE_APP_API + url + "&filterKey=" + filter_key, null, true);

        }
    },
    GetAllUsers(url) {
        if (url) {
            return http.get(process.env.VUE_APP_API + url, null, true, "Could not get users list");
        }
        return http.get(process.env.VUE_APP_API + "user/getPaginated", null, true, "Could not get users list");
    },
  
    CancelService(serviceId) {
        return http.post(process.env.VUE_APP_API + "service/cancel/" + serviceId, null, true, "Could not cancel service");
    },
    GetFollowersByUserId(id) {
        return http.get(process.env.VUE_APP_API + "user/" + id + "/follower", null, false);
    },
    GetFollowingsByUserId(id) {
        return http.get(process.env.VUE_APP_API + "user/" + id + "/following", null, false);
    },
    GetUrl(url) {
        console.log("url: ", process.env.VUE_APP_API +url)

        return http.get(process.env.VUE_APP_API +url, null, false);
    },
    GetServiceDashboard(){
        return http.get(process.env.VUE_APP_API + "service/dashboard/all", null, true);

    }
}