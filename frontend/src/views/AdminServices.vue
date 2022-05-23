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
              <h3>Services</h3>
            </div>
            <div class="mt-5 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-12">
                  <div class="container ct-example-row">
                    <div>
                      <table class="table table-striped">
                        <thead class="">
                          <tr>
                            <th scope="col">Name</th>
                            <th scope="col">Owner</th>
                            <th scope="col">Date</th>
                            <th scope="col">View</th>
                            <th scope="col">Featured</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr
                            v-for="(service, index) in allServices"
                            :key="index"
                          >
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
                              <base-button
                                v-if="service.featured"
                                block
                                type="warning"
                                @click="RemoveFeatured(service.id)"
                                class="mb-3"
                              >
                                Unfeature
                              </base-button>

                              <base-button
                                v-else
                                block
                                type="success"
                                @click="AddFeatured(service.id)"
                                class="mb-3"
                              >
                                Feature
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
                </div>
              </div>
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
import infiniteLoading from "vue-infinite-loading";
import BackToTop from "vue-backtotop";
import swal from "sweetalert2";

export default {
  components: {
    BaseButton,
    infiniteLoading,
    BackToTop,
  },
  data() {
    return {
      allServices: [],
      next: null,
    };
  },
  mounted() {
    this.GetAllServices();
  },
  computed: {},
  methods: {
    infiniteHandler($state) {
      console.log("called infinite handler");
      if (!this.next) {
        console.log("no next found");
        setTimeout(() => {
            console.log("recalling infinite handler");
            this.infiniteHandler($state);
          }, 500);
        return;
      }
      console.log("will call url", this.next);
      apiRegister
        .GetAllServicesSorted(false, "all", null, this.next,20)
        .then((r) => {
          if (r.items.length) {
            setTimeout(() => {
              this.next = r.nextPage;
              var merged = [...this.allServices, ...r.items];
              this.allServices = merged;
              $state.loaded();
              console.log("svc count: ", this.allServices.length);
            }, 1000);
          } else {
            $state.complete();
          }
        });
    },
    GetAllServices() {
      apiRegister.GetAllServices(false, "all").then((r) => {
        this.allServices = r.items;
        this.next = r.nextPage;
        console.log("response", r);
      });
    },
    GoToService(serviceId) {
      var url = "#/service/" + serviceId;
      window.location.href = url;
    },
    AddFeatured(serviceId) {
      apiRegister.FeatureService(serviceId).then((r) => {
        // swal.fire({
        //   position: "top-end",
        //   icon: "success",
        //   title: "Service has been added to featured",
        //   showConfirmButton: false,
        //   timer: 1500,
        // });
        this.GetAllServices();
      });
    },
    RemoveFeatured(serviceId) {
      apiRegister.UnfeatureService(serviceId).then((r) => {
        // swal.fire({
        //   position: "top-end",
        //   icon: "success",
        //   title: "Service has been added to featured",
        //   showConfirmButton: false,
        //   timer: 1500,
        // });
        this.GetAllServices();
      });
    },
  },
};
</script>
<style>
</style>
