import Vue from "vue";
import Router from "vue-router";
import AppHeader from "./layout/AppHeader";
import AppFooter from "./layout/AppFooter";
import AdminAppHeader from "./layout/AdminAppHeader";
import AdminAppFooter from "./layout/AdminAppFooter";
import Components from "./views/Components.vue";
import Landing from "./views/Landing.vue";
import Home from "./views/Home.vue";
import Login from "./views/Login.vue";
import Register from "./views/Register.vue";
import Profile from "./views/Profile.vue";
import UserPage from "./views/UserPage.vue";
import CreateService from "./views/CreateService.vue";
import SingleService from "./views/SingleService.vue";
import MyServices from "./views/MyServices.vue";
import AllServices from "./views/AllServices.vue";
import AttendingServices from "./views/AttendingServices.vue";
import FollowingUserServices from "./views/FollowingUserServices.vue";
import PendingRequests from "./views/PendingRequests.vue";
import Notifications from "./views/Notifications.vue";
import AdminDashboard from "./views/AdminDashboard.vue";
Vue.use(Router);

const ifNotAuthenticated = (to, from, next) => {
  let token = JSON.parse(localStorage.getItem("token"));
  if (!token) {   
    next()

  }
  next('/')
}

const ifAuthenticated = (to, from, next) => {

  let token = JSON.parse(localStorage.getItem("token"));
  if (token) {
    next()
    return
  }
  next('/login')
}

export default new Router({
  linkExactActiveClass: "active",
  routes: [
    {
      path: "/",
      name: "home",
      components: {
        header: AppHeader,
        default: Home,
        footer: AppFooter
      },
    }, {
      path: "/components",
      name: "components",
      components: {
        header: AppHeader,
        default: Components,
        footer: AppFooter,

      }
    },
    {
      path: "/landing",
      name: "landing",
      components: {
        header: AppHeader,
        default: Landing,
        footer: AppFooter
      }
    },
    {
      path: "/login",
      name: "login",
      components: {
        header: AppHeader,
        default: Login,
        footer: AppFooter
      }
    },
    {
      path: "/register",
      name: "register",
      components: {
        header: AppHeader,
        default: Register,
        footer: AppFooter,
        // beforeEnter: ifNotAuthenticated,
      }
    },
    {
      path: "/defaultProfile",
      name: "profile",
      components: {
        header: AppHeader,
        default: Profile,
        footer: AppFooter,
        // beforeEnter: ifAuthenticated,

      }
    },
    {
      path: "/profile/:userId?",
      name: "userpage",
      components: {
        header: AppHeader,
        default: UserPage,
        footer: AppFooter,
        

      }
    },
    {
      path: "/createService",
      name: "createService",
      components: {
        header: AppHeader,
        default: CreateService,
        footer: AppFooter,
        

      }
    },
    {
      path: "/service/:service_id",
      name: "singleService",
      components: {
        header: AppHeader,
        default: SingleService,
        footer: AppFooter,

      }
    },
    {
      path: "/myServices",
      name: "myServices",
      components: {
        header: AppHeader,
        default: MyServices,
        footer: AppFooter,

      }
    },
    {
      path: "/pendingRequests",
      name: "pendingRequests",
      components: {
        header: AppHeader,
        default: PendingRequests,
        footer: AppFooter,
      }
    },
    ,
    {
      path: "/myNotifications",
      name: "myNotifications",
      components: {
        header: AppHeader,
        default: Notifications,
        footer: AppFooter,
      }
    },    
    {
      path: "/allServices",
      name: "allServices",
      components: {
        header: AppHeader,
        default: AllServices,
        footer: AppFooter,

      }
    },
    {
      path: "/attendingServices",
      name: "attendingServices",
      components: {
        header: AppHeader,
        default: AttendingServices,
        footer: AppFooter,

      }
    },

    {
      path: "/followingUserServices",
      name: "followingUserServices",
      components: {
        header: AppHeader,
        default: FollowingUserServices,
        footer: AppFooter,

      }
    },
    {
      path: "/adminDashboard",
      name: "adminDashboard",
      components: {
        header: AdminAppHeader,
        default: AdminDashboard,
        footer: AdminAppFooter,

      }
    },

    
  ],
  scrollBehavior: to => {
    if (to.hash) {
      return { selector: to.hash };
    } else {
      return { x: 0, y: 0 };
    }
  }
});
