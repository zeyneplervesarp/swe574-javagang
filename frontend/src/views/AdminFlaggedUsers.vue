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
                        <th scope="col">Username</th>
                        <th scope="col">Bio</th>
                        <th scope="col">Rating</th>
                        <th scope="col">FlagCount</th>
                        <th scope="col">View</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(user, index) in flaggedUsers" :key="index">
                        <td>{{ user.username }}</td>
                        <td>{{ user.bio }}</td>
                        <td>
                          <span v-if="user.ratingSummary.ratingAverage == 0">
                            no rating
                          </span>
                          <span v-else>
                            {{ FormatDouble(user.ratingSummary.ratingAverage) }} / 5
                          </span>
                        </td>
                        <td>{{ user.flagCount }}</td>
                        <td>
                          <base-button
                            block
                            type="primary"
                            class="mb-3"
                            @click="GoToProfile(user.id)"
                          >
                            <i class="ni ni-curved-next"></i>
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
    FormatDouble(num) {
      return Math.round(num * 10) / 10;
    },
    GetAllFlaggedUsers() {
      apiRegister.GetAllFlaggedUsers().then((r) => {
        this.flaggedUsers = r;
      });
    },
    GoToProfile(userId) {
      var url = "#/profile/" + userId;
      window.location.href = url;
    },
  },
};
</script>
<style>
</style>
