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
                    Users              
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
                        <th scope="col">E-mail</th>
                        <th scope="col">Description</th>
                        <th scope="col">View</th>
                        <th scope="col">Ban</th>
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
                            View User
                          </base-button>
                        </td>
                        <td>
                          <base-button block type="warning" @click="AddFeatured(service.id)" class="mb-3">
                            Ban
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
      allUsers: [],
    };
  },
  mounted() {
   this.GetAllUsers();
  },
  computed: {},
  methods: {
    GoToUserProfile(userId) {
      var url = "#/profile/" + userId;
      window.location.href = url;
    },
    GetAllUsers(){
      debugger;
      apiRegister.GetAllUsers().then((r)=>{
        this.allUsers = r;
      });
    }
  },
};
</script>
<style>
</style>
