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
import EditService from "./views/EditService.vue";
import SingleService from "./views/SingleService.vue";
import MyServices from "./views/MyServices.vue";
import AllServices from "./views/AllServices.vue";
import AttendingServices from "./views/AttendingServices.vue";
import FollowingUserServices from "./views/FollowingUserServices.vue";
import PendingRequests from "./views/PendingRequests.vue";
import Notifications from "./views/Notifications.vue";
import AdminServices from "./views/AdminServices.vue";
import AdminUsers from "./views/AdminUsers.vue";
import AdminStats from "./views/AdminStats.vue";
import Search from "./views/Search.vue";
import AdminFlaggedServices from "./views/AdminFlaggedServices.vue";
import AdminFlaggedUsers from "./views/AdminFlaggedUsers.vue";
import AdminFeed from "./views/AdminFeed.vue";

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
      path: "/service/edit/:service_id",
      name: "editService",
      components: {
        header: AppHeader,
        default: EditService,
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
      path: "/admin/services",
      name: "adminServices",
      components: {
        header: AdminAppHeader,
        default: AdminServices,
        footer: AdminAppFooter,
      }
    },
    {
      path: "/admin/feed",
      name: "adminFeed",
      components: {
        header: AdminAppHeader,
        default: AdminFeed,
        footer: AdminAppFooter,
      }
    },
    {

      path: "/admin/users",
      name: "adminUsers",
      components: {
        header: AdminAppHeader,
        default: AdminUsers,
        footer: AdminAppFooter,
      }
    },
    {
      path: "/admin/stats",
      name: "adminStats",
      components: {
        header: AdminAppHeader,
        default: AdminStats,
        footer: AdminAppFooter,
      }
    },
    {
      path: "/search/:search_query",
      name: "search",
      components: {
        header: AppHeader,
        default: Search,
        footer: AppFooter,
      }
    },
    {
      path: "/admin/flagged_services",
      name: "adminFlaggedServices",
      components: {
        header: AdminAppHeader,
        default: AdminFlaggedServices,
        footer: AdminAppFooter,

      }
    },
    {
      path: "/admin/flagged_users",
      name: "adminFlaggedUsers",
      components: {
        header: AdminAppHeader,
        default: AdminFlaggedUsers,
        footer: AdminAppFooter,
      }
    }

    
  ],
  scrollBehavior: to => {
    if (to.hash) {
      return { selector: to.hash };
    } else {
      return { x: 0, y: 0 };
    }
  }
});
