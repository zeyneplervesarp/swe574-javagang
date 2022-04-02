<template>
  <div class="profile-page">
    <section class="section-profile-cover section-shaped my-0">
      <div class="shape shape-style-1 shape-primary shape-skew alpha-4">
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
              >
                <!-- <div class="card-profile-actions py-4 mt-lg-0">
                  <base-button
                    v-if="
                      !userData.hasServiceRequest &&
                      !userData.ownsService &&
                      !serviceData.showServiceButton
                    "
                    @click="ConfirmRequest"
                    type="info"
                    size="sm"
                    class="mr-4"
                    >Send Request to Join</base-button
                  >
                  <base-button
                    disabled
                    v-if="userData.hasServiceRequest"
                    type="info"
                    size="sm"
                    class="mr-4"
                    >Already requested to join</base-button
                  >
                  <base-button
                    disabled
                    v-if="
                      serviceData.showServiceButton && !userData.ownsService
                    "
                    type="warning"
                    size="sm"
                    class="mr-4"
                    >This service has passed</base-button
                  >
               
                </div> -->
              </div>
              <!-- <div class="col-lg-4 order-lg-1">
                <div class="card-profile-stats d-flex justify-content-center">
                  <div @click="OpenParticipantModal()">
                    <a href="#"
                      ><span class="heading">{{
                        serviceData.attendingUserCount
                      }}</span>
                      <span class="description">Participants</span>
                    </a>
                  </div>
                  <div>
                    <span class="heading">{{ serviceData.quota }}</span>
                    <span class="description">Quota</span>
                  </div>
                  <div>
                    <span class="heading">{{ serviceData.minutes }}</span>
                    <span class="description">Credits</span>
                  </div>
                </div>
              </div> -->
            </div>
            <div class="text-center mt-5">
              <h3>
                <!-- {{ serviceData.header }}
                <span class="font-weight-light">
                  <a :href="'#/profile/' + serviceData.createdUserIdId">
                    by {{ serviceData.createdUserName }}</a
                  ></span
                > -->
              </h3>
              <div></div>
              <br />
              <div class="text-center">
                <div>
                  <table class="table">
                    <thead class="thead-dark">
                      <tr>
                        <th scope="col">#</th>
                        <th scope="col">Name</th>
                        <th scope="col">Owner</th>
                        <th scope="col">Date</th>
                        <th scope="col">View</th>
                        <th scope="col">Featured</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(service, index) in allServices" :key="index">
                        <th scope="row">{{ index + 1 }}</th>
                        <td>{{ service.header }}</td>
                        <td>{{ service.createdUserName }}</td>
                        <td>{{ service.timeString }}</td>
                        <td>
                          <base-button
                            block
                            type="primary"
                            class="mb-3"
                            @click="GoToService(service.id)"
                          >
                            View
                          </base-button>
                        </td>
                        <td>
                          <base-button block type="warning" @click="AddFeatured(service.id)" class="mb-3">
                            Featured
                            {{service.id}}
                          </base-button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <br />
              <div>
                <!-- <i class="ni ni-square-pin"></i> : {{ serviceData.location }} -->
                <!-- <i class="ni ni-time-alarm"></i>: {{ serviceData.timeString }} -->
              </div>
              <!-- <div>
                <i class="ni ni-single-02"></i>: {{ serviceData.quota }} people
              </div> -->
            </div>
            <div class="mt-2 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>
                    <!-- {{ serviceData.description }} -->
                  </p>

                  <div>
                  </div>
                </div>
              </div>
            </div>
            <div class="mt-2 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>
                    Want to get more information about the tags? Click on the
                    tag that you would like to get more information about.
                  </p>

                  <div>
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
import BaseButton from "../components/BaseButton.vue";
import apiRegister from "../api/register";
import swal from "sweetalert2";

export default {
  components: { BaseButton },
  data() {
    return {
      allServices: [],
    };
  },
  mounted() {
    this.GetAllServices();
  },
  computed: {},
  methods: {
    GetAllServices() {
      apiRegister.GetAllServices(false, "first3").then((r) => {
        this.allServices = r;
      });
    },
    GoToService(serviceId) {
      var url = "#/service/" + serviceId;
      window.location.href = url;
    },
    AddFeatured(serviceId) {
      // apiRegister.AddServiceToFeatured(serviceId).then((r) => {
       
      // });
       swal.fire({
          position: "top-end",
          icon: "success",
          title: "Service has been added to featured",
          showConfirmButton: false,
          timer: 1500,
        });
    },
  },
};
</script>
<style>
</style>
