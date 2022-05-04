<template>
<div style="position: relative">
<base-nav type="default" effect="dark" expand>
    <a class="navbar-brand" href="/">SocialHub Admin</a>

    <div class="row" slot="content-header" slot-scope="{closeMenu}">
        <div class="col-6 collapse-brand">
            <a href="https://demos.creative-tim.com/vue-argon-design-system/documentation/">
                <img src="https://demos.creative-tim.com/vue-argon-design-system/img/brand/blue.png">
            </a>
        </div>
        <div class="col-6 collapse-close">
            <close-button @click="closeMenu"></close-button>
        </div>
    </div>

    <ul class="navbar-nav ml-lg-auto">
        <li class="nav-item">
            <a class="nav-link nav-link-icon" href="#/admin/services">
                Services
            </a>
        </li>
        <li class="nav-item">

            <a class="nav-link nav-link-icon" href="#/admin/feed">
                Feed
                  </a>
        </li>
        <li class="nav-item">
            <a class="nav-link nav-link-icon" href="#/admin/users">
                Users
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link nav-link-icon" href="#">
                Profile

            </a>
        </li>
       
    </ul>
 </base-nav>
</div>
</template>
<script>
import BaseNav from "@/components/BaseNav";
import BaseDropdown from "@/components/BaseDropdown";
import CloseButton from "@/components/CloseButton";
import apiRegister from "@/api/register.js";

export default {
  data() {
    return {
      userLoggedIn: "",
      notificationMessage: "You have no new messages",
      hasNewNotification: false,
      r: {},
    };
  },
  mounted() {
    var foo = process.env.VUE_APP_GOOGLE_MAP_KEY;
    console.log("google keys");
    console.log(foo);
    let token = JSON.parse(localStorage.getItem("token"));

    if (token) {
      this.userLoggedIn = true;
      apiRegister.GetNotificationDetails().then((notificationList) => {
        var unreadCount = notificationList.filter(
          (notificationList) => notificationList.read === false
        ).length;
        if (unreadCount > 0) {
          this.hasNewNotification = true;
          this.notificationMessage =
            "You have " + unreadCount + " new messages";
        }
      });
    } else {
      this.userLoggedIn = false;
    }
  },
  components: {
    BaseNav,
    CloseButton,
    BaseDropdown,
  },
  methods: {
    EmptyLocalStorage() {
      localStorage.clear();
      document.location.href = "../";
    },
  },
};
</script>
<style>
</style>

