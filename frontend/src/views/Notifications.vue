<template>
  <div class="profile-page">
    <section class="section-profile-cover section-shaped my-0">
      <div class="shape shape-style-2 shape-primary shape-skew alpha-4">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </section>
    <section class="section section-skew">
      <div class="container">
        <card shadow class="card-profile mt--300" no-body>
          <div class="px-4">
            <div class="row justify-content-center">
              <div class="col-lg-3 order-lg-2"></div>
            </div>
            <div class="text-center mt-5">
              <h4>
                Notifications
                <span class="font-weight-light"
                  >({{ unreadCount }} unread)</span
                >
              </h4>
            </div>
            <div class="mt-5 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-12">
                  <div class="container ct-example-row">
                    <div
                      class="row"
                      v-for="(notification1, index) in unreadNotifications"
                      :key="index"
                    >
                      <div class="col mt-2 text-center">
                        <span>
                          <base-button 
                          @click="GoToUrl(notification1.messageBody)" 
                          block 
                          type="secondary"
                          style="text-align: left;  text-transform: lowercase; background-color: #A16FD0"
                          >{{getFormattedDate(notification1.sentDate)}} | {{notification1.message}}</base-button>
                        </span>
                      </div>
                      <div class="w-100"></div>
                    </div>
                    <div
                      class="row"
                      v-for="(notification, index) in readNotifications"
                      :key="index"
                    >
                      <div class="col mt-2 text-center">
                        <span>
                          <base-button  
                          @click="GoToUrl(notification.messageBody)" 
                          block  
                          type="succeess"
                          style="text-align: left;  text-transform: lowercase; background-color: #D0C5DA"
                          >{{getFormattedDate(notification.sentDate)}} | {{
                            notification.message
                          }}</base-button>
                        </span>
                      </div>
                      <div class="w-100"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </card>
      </div>
    </section>
  </div>
</template>
<script>
import apiRegister from "../api/register";
import moment from 'moment';
export default {
  components: {},
  data() {
    return {
      notifications: [],
      unreadNotifications: [],
      readNotifications: [],
      unreadCount: 0,
      firstUpdate: true,
    };
  },
  mounted() {
    this.GetNotifications();
  },
  updated(){
    if(firstUpdate==true){
      this.firstUpdate = false;
    }else{
      this.GetNotifications();
    }
  },
  methods: {
    GetNotifications() {
      apiRegister.GetNotificationDetails().then((notificationList) => {
        this.notifications = notificationList;
        this.unreadNotifications = notificationList.filter(
          (notificationList) => notificationList.read === false
        );
        this.unreadCount = this.unreadNotifications.length;
        this.readNotifications = notificationList.filter(
          (notificationList) => notificationList.read === true
        );
        apiRegister.ReadAllNotifications();
      });
    },
    getFormattedDate(date) {
            return moment(date).format("YYYY-MM-DD")
    },
    GoToUrl(url)
    {
      window.location.href = "#" + url;
    }
  },
  destroyed() {
          window.location.reload();

  },
  props: {
    userId: String,
  },
};
</script>
<style>
</style>
