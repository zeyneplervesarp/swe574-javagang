<template>
  <div class="profile-page">
    <section class="section-profile-cover section-shaped my-0">
      <div class="shape shape-style-3 shape-primary shape-skew alpha-4">
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
              <div
                class="col-lg-4 order-lg-3 text-lg-right align-self-lg-center"
              ></div>
            </div>
            <div class="text-center mt-5">
              <h3>Users</h3>
              <div></div>
              <br />
              <div class="text-center">
                <div style="overflow-x: auto">
                  <table
                    class="table table-striped"
                    style="width: 100%; table-layout: fixed"
                  >
                    <thead class="">
                      <tr>
                        <th scope="col" style="width: 10%">#</th>
                        <th scope="col" style="width: 10%">Username</th>
                        <th scope="col" style="width: 15%">E-mail</th>
                        <th scope="col" style="width: 40%">Description</th>
                        <th scope="col" style="width: 10%">View</th>
                        <th scope="col" style="width: 10%">Ban</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(user, index) in allUsers" :key="index">
                        <th scope="row">{{ index + 1 }}</th>
                        <td>{{ user.username }}</td>
                        <td>{{ user.email }}</td>
                        <td>{{ user.bio }}</td>
                        <td>
                          <base-button
                            block
                            type="primary"
                            class="mb-3"
                            @click="GoToUserProfile(user.id)"
                          >
                            <i class="ni ni-curved-next"></i>
                          </base-button>
                        </td>
                        <td>
                          <base-button
                            block
                            type="warning"
                            @click="DeleteUser(user.id)"
                            class="mb-3"
                          >
                            <i class="ni ni-bell-55"></i>
                          </base-button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <infinite-loading
                  @infinite="infiniteHandler"
                  spinner="spiral"
                ></infinite-loading>
              </div>
              <br />
              <div></div>
            </div>
          </div>
        </card>
      </div>
    </section>
    <back-to-top text="Back to top" visibleoffset="500"></back-to-top>
  </div>
</template>
<script>
import BaseButton from "../components/BaseButton.vue";
import apiRegister from "../api/register";
import swal from "sweetalert2";
import infiniteLoading from "vue-infinite-loading";
import BackToTop from "vue-backtotop";

export default {
  components: {
    BaseButton,
    infiniteLoading,
    BackToTop,
  },
  data() {
    return {
      allUsers: [],
      next: null,
    };
  },
  mounted() {
    this.GetAllUsers();
  },
  computed: {},
  methods: {
    infiniteHandler($state) {
      if (!this.next) return;
      console.log("will call url", this.next);
      apiRegister.GetAllUsers(this.next).then((r) => {
        if (r.items.length) {
          setTimeout(() => {
            this.next = r.nextPage;
            var merged = [...this.allUsers, ...r.items];
            this.allUsers = merged;
            $state.loaded();
            console.log("user count: ", this.allUsers.length);
          }, 1000);
        } else {
          $state.complete();
        }
      });
    },
    GoToUserProfile(userId) {
      var url = "#/profile/" + userId;
      window.location.href = url;
    },
    GetAllUsers() {
      apiRegister.GetAllUsers(null).then((r) => {
        this.allUsers = r.items;
        this.next = r.nextPage;
      });
    },
    DeleteUser(userId) {
      apiRegister.DeleteUser(userId).then((r) => {
        this.$router.go();
      });
    },
  },
};
</script>
<style>
</style>
