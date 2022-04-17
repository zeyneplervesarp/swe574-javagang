<template>
  <header class="header-global">
    <base-nav class="navbar-main" transparent type="" effect="light" expand>
      <router-link slot="brand" class="navbar-brand mr-lg-5" to="/">
        <img
          style="height='45px !important'"
          src="img/brand/foo.png"
          alt="logo"
        />
      </router-link>

      <div class="row" slot="content-header" slot-scope="{ closeMenu }">
        <div class="col-6 collapse-brand">
          <a
            href="https://demos.creative-tim.com/vue-argon-design-system/documentation/"
          >
            <img src="img/brand/blue.png" />
          </a>
        </div>
        <div class="col-6 collapse-close">
          <close-button @click="closeMenu"></close-button>
        </div>
      </div>

      <ul class="navbar-nav navbar-nav-hover align-items-lg-center">
        <base-dropdown
          v-if="userLoggedIn"
          class="nav-item"
          menu-classes="dropdown-menu-xl"
        >
          <a
            slot="title"
            href="#"
            class="nav-link"
            data-toggle="dropdown"
            role="button"
          >
            <i class="ni ni-ui-04 d-lg-none"></i>
            <span class="nav-link-inner--text">Services</span>
          </a>
          <div class="dropdown-menu-inner">
            <a href="#/allServices" class="media d-flex align-items-center">
              <div
                class="
                  icon icon-shape
                  bg-gradient-primary
                  rounded-circle
                  text-white
                "
              >
                <i class="ni ni-spaceship"></i>
              </div>
              <div class="media-body ml-3">
                <h6 class="heading text-primary mb-md-1">All Services</h6>
                <p class="description d-none d-md-inline-block mb-0">
                  Browse all the services on the socialhub
                </p>
              </div>
            </a>
            <a href="#/myServices" class="media d-flex align-items-center">
              <div
                class="
                  icon icon-shape
                  bg-gradient-success
                  rounded-circle
                  text-white
                "
              >
                <i class="ni ni-badge"></i>
              </div>
              <div class="media-body ml-3">
                <h5 class="heading text-success mb-md-1">Created Services</h5>
                <p class="description d-none d-md-inline-block mb-0">
                  List all the services that you have created so far
                </p>
              </div>
            </a>
            <a
              href="#/attendingServices"
              class="media d-flex align-items-center"
            >
              <div
                class="
                  icon icon-shape
                  bg-gradient-warning
                  rounded-circle
                  text-white
                "
              >
                <i class="ni ni-world-2"></i>
              </div>
              <div class="media-body ml-3">
                <h5 class="heading text-warning mb-md-1">Attending Services</h5>
                <p class="description d-none d-md-inline-block mb-0">
                  List all the services that you are attending so far
                </p>
              </div>
            </a>
            <a
              href="#/followingUserServices"
              class="media d-flex align-items-center"
            >
              <div
                class="
                  icon icon-shape
                  bg-gradient-default
                  rounded-circle
                  text-white
                "
              >
                <i class="ni ni-badge"></i>
              </div>
              <div class="media-body ml-3">
                <h5 class="heading text-default mb-md-1">
                  Following User Services
                </h5>
                <p class="description d-none d-md-inline-block mb-0">
                  List of the services that the people you are following have
                  created
                </p>
              </div>
            </a>
          </div>
        </base-dropdown>
        <base-input
          v-model="searchQuery"
          v-on:keyup.enter="OnEnter()"
          class="mt-3"
          alternative
          placeholder="Search"
          addon-right-icon="ni ni-zoom-split-in"
        ></base-input>
      </ul>
      <ul class="navbar-nav align-items-lg-center ml-lg-auto">
        <li v-if="userLoggedIn" class="nav-item">
          <a
            class="nav-link nav-link-icon"
            href="#/myNotifications"
            data-toggle="tooltip"
            v-bind:title="notificationMessage"
          >
            <i
              v-bind:class="hasNewNotification ? 'fa fa-bell' : 'fa fa-bell-o'"
            ></i>
          </a>
        </li>

        <li v-if="!userLoggedIn" class="nav-item d-none d-lg-block ml-lg-4">
          <a href="#/register" rel="noopener" class="btn btn-neutral btn-icon">
            <span class="btn-inner--icon">
              <i class="fa fa-user mr-2"></i>
            </span>
            <span class="nav-link-inner--text">Register</span>
          </a>
        </li>

        <li v-if="!userLoggedIn" class="nav-item d-none d-lg-block ml-lg-4">
          <a href="#/login" rel="noopener" class="btn btn-neutral btn-icon">
            <span class="btn-inner--icon">
              <i class="fa fa-magic mr-2"></i>
            </span>
            <span class="nav-link-inner--text">Log In </span>
          </a>
        </li>
        <li
          v-if="userLoggedIn && userIsAdmin"
          class="nav-item d-none d-lg-block ml-lg-4"
        >
          <a
            href="#/admin/services"
            rel="noopener"
            class="btn btn-default btn-icon"
          >
            <span class="nav-link-inner--text">Admin</span>
          </a>
        </li>
        <li>
          <base-dropdown v-if="userLoggedIn" class="nav-item">
            <base-button
              slot="title"
              type="secondary"
              class="dropdown-toggle btn btn-neutral btn-icon"
            >
              Me
            </base-button>
            <router-link to="/profile" class="dropdown-item"
              >My Profile</router-link
            >

            <div class="dropdown-divider"></div>
            <router-link to="/createService" class="dropdown-item"
              >Create Service</router-link
            >
            <!-- <router-link to="/myServices" class="dropdown-item"
              >My Services</router-link
            > -->
            <router-link to="/pendingRequests" class="dropdown-item"
              >Pending Requests</router-link
            >
            <div class="dropdown-divider"></div>

            <a
              href="#"
              v-on:click="EmptyLocalStorage"
              rel="noopener"
              class="btn btn-neutral btn-icon"
            >
              <span class="btn-inner--icon">
                <i class="fa fa-meh mr-2"></i>
              </span>
              <span class="nav-link-inner--text">Log Out</span>
            </a>
          </base-dropdown>
        </li>
      </ul>
    </base-nav>
  </header>
</template>
<script>
import BaseNav from "@/components/BaseNav";
import BaseDropdown from "@/components/BaseDropdown";
import CloseButton from "@/components/CloseButton";
import apiRegister from "@/api/register.js";
import swal from "sweetalert2";

export default {
  data() {
    return {
      userLoggedIn: "",
      notificationMessage: "You have no new messages",
      hasNewNotification: false,
      r: {},
      userIsAdmin: false,
      searchQuery: "",
    };
  },
  mounted() {
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
      apiRegister.GetProfile().then((r) => {
        var compare = r.userType.localeCompare("ADMIN");
        this.userIsAdmin = compare == 0;
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
    OnEnter() {
      if (this.searchQuery.length == 0) {
        swal.fire({
          icon: "error",
          title: "Oops...",
          text: "Please enter a keyword to search!",
        });
      }
      console.log(this.searchQuery);


      var url = "#/search/" + this.searchQuery;
      window.location.href = url;

      // apiRegister.Search(this.searchQuery);
    },
  },
};
</script>
<style>
</style>

