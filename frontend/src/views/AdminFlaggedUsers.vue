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
                class="col-lg-4 order-lg-3 text-lg-right align-self-lg-center">
              </div>
            </div>
            <div class="text-center mt-5">
              <h3>
                    Flagged Users              
              </h3>
              <div></div>
              <br />
              <div class="text-center">
                <div>
                  <table class="table table-striped">
                    <thead class="">
                      <tr>
                        <th scope="col">#</th>
                        <th scope="col">Username</th>
                        <th scope="col">FlagCount</th>
                        <th scope="col">View</th>
                        <th scope="col">Delete</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(user, index) in flaggedUsers" :key="index">
                        <th scope="row">{{ index + 1 }}</th>
                        <td>{{ user.username }}</td>
                        <td>{{ user.flagCount }}</td>
                        <td>
                          <base-button
                            block
                            type="primary"
                            class="mb-3"
                            @click="GoToProfile(user.id)"
                          >
                            View
                          </base-button>
                        </td>
                        <td>
                          <base-button 
                            block 
                            type="warning" 
                            class="mb-3"
                            @click="DeleteUser(user.id)">
                            Delete
                          </base-button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <br />
              <div>
              </div>
            </div>
          </div>
        </card>
      </div>
    </section>
  </div>
</template>
<script>
import BaseButton from "../components/BaseButton.vue";
import apiRegister from "../api/register";
import swal from "sweetalert2";

export default {
  components: { BaseButton },
  data() {
    return {
      flaggedUsers: [],
    };
  },
  mounted() {
    this.GetAllFlaggedUsers();
  },
  computed: {},
  methods: {
    GetAllFlaggedUsers() {
      apiRegister.GetAllFlaggedUsers().then((r) => {
        this.flaggedUsers = r;
      });
    },
    GoToProfile(userId) {
      var url = "#/profile/" + userId;
      window.location.href = url;
    },
    DeleteUser(userId) {
        apiRegister.DeleteUser(userId).then((r) => {
          location.reload();
      });
    }
  },
};
</script>
<style>
</style>
