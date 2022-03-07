<template>
  <div>
    <div class="position-relative">
      <section class="section section section-shaped my-0 overflow-hidden">
        <div class="shape shape-style-1 bg-gradient-success shape-skew">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>

          <span></span>
        </div>
        <div class="container py-0">
          <div class="row row-grid align-items-center">
            <div class="col-md-6 order-lg-2 ml-lg-auto">
              <div class="position-relative pl-md-5">
                <img
                  src="img/ill/ill-2.svg"
                  class="img-center img-fluid"
                  style="opacity: 0"
                />
              </div>
            </div>
            <div class="col-lg-6 order-lg-1">
              <div class="d-flex px-3">
                <div>
                  <icon
                    name="ni ni-notification-70"
                    size="lg"
                    class="bg-gradient-white"
                    color="success"
                    shadow
                    rounded
                  ></icon>
                </div>
                <div class="pl-4">
                  <h4 class="display-3 text-white">
                    Requests that need your approval
                  </h4>
                  <p class="text-white">
                    Here you can find the requests to the services that you have
                    created. You can confirm or deny them.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <section class="section section-lg pt-lg-0 mt--400">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-lg-12">
            <div
              v-for="(array, index) in nestedArray"
              :key="index"
              class="row row-grid"
            >
              <div
                v-for="(approvalItem, index) in array"
                :key="index"
                class="col-lg-4"
              >
                <card class="border-0" hover shadow body-classes="py-5">
                  <!-- <icon
                    v-bind:type="GetClass(index)"
                    v-bind:name="GetIcon(index)"
                    rounded
                    class="mb-4"
                  >
                  </icon> -->
                  <h6 class="text-success text-uppercase">
                    {{ approvalItem.service.header }}
                  </h6>
                  <p class="description mt-3" :href="'#/profile/' + approvalItem.user.id"
 >
                    Requested by: {{ approvalItem.user.username }}
                  </p>
                  <div class="row text-center">
                    <base-button
                      tag="a"
                      v-on:click="
                        Approve(approvalItem.user.id, approvalItem.service.id)
                      "
                      href="#"
                      type="success"
                      class="col-sm"
                    >
                      Approve
                    </base-button>
                    <base-button
                      tag="a"
                      v-on:click="
                        Deny(approvalItem.user.id, approvalItem.service.id)
                      "
                      href="#"
                      type="warning"
                      class="col-sm"
                    >
                      Deny
                    </base-button>
                  </div>
                </card>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section section-lg pt-0">
      <div class="container"></div>
    </section>
  </div>
</template>

<script>
import apiRegister from "@/api/register";

export default {
  data() {
    return {
      result: [],
      nestedArray: [],
      approveItem: {
        userId: 0,
        serviceId: 0,
      },
    };
  },
  mounted() {
    this.GetApprovalList();
  },
  methods: {
    GetApprovalList() {
      apiRegister.GetApprovalListByUser().then((response) => {
        this.result = response;
        this.nestedArray = this.SplitList();
      });
    },
    SplitList() {
      var array = this.result;
      var listOfArrays = [];

      var i,
        j,
        temporary,
        chunk = 3;
      for (i = 0, j = array.length; i < j; i += chunk) {
        temporary = array.slice(i, i + chunk);
        listOfArrays.push(temporary);
      }
      return listOfArrays;
    },

    GetIcon(index) {
      var i = index + (1 % 3);
      if (i == 1) {
        return "ni ni-check-bold";
      } else if (i == 2) {
        return "ni ni-istanbul";
      } else {
        return "ni ni-planet";
      }
    },
    Approve(userId, serviceId) {
      this.approveItem.userId = userId;
      this.approveItem.serviceId = serviceId;
      apiRegister.ApproveRequest(this.approveItem).then((response) => {
        alert("Request Approved");
        window.location.reload();
      });
    },
    Deny(userId, serviceId) {
      this.approveItem.userId = userId;
      this.approveItem.serviceId = serviceId;
      apiRegister.DenyRequest(this.approveItem).then((response) => {
        alert("Request Denied");
        window.location.reload();
      });
    },
  },
  components: {},
};
</script>
