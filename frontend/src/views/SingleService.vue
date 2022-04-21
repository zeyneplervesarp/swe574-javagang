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
                <div class="card-profile-actions py-4 mt-lg-0">
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
                  <!-- <base-button type="default" size="sm" class="float-right"
                    >Message</base-button
                  > -->
                </div>
              </div>
              <div class="col-lg-4 order-lg-1">
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
                  <div v-if="userIsAdmin">
                    <span class="heading">{{ serviceData.flagCount }}</span>
                    <span class="description">FlagCount</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="text-center mt-5">
              <h3>
                {{ serviceData.header }}
                <span class="font-weight-light">
                  <a :href="'#/profile/' + serviceData.createdUserIdId">
                    by {{ serviceData.createdUserName }}</a
                  ></span
                >
              </h3>
              <div></div>
              <br />
         <div class="text-center" v-if="serviceData.locationType === ''">
                <base-button
                  v-if="serviceData.formattedAddress != ''"
                  type="secondary"
                >
                  <img v-bind:src=serviceData.imageUrl>
                </base-button>
              </div>

          
              <div class="text-center">
                <p> Meeting Link: {{serviceData.location}} </p>
              </div>
              <br />

              <div>
                <i class="ni ni-time-alarm"></i>: {{ serviceData.timeString }}
              </div>
              <div   v-if="
                userData.attendsService &&
                serviceData.status === 'COMPLETED'
              "
              class="row justify-content-center">
                <star-rating
                  :star-size="20"
                  @rating-selected="SetRating"
                  :read-only="ratingData.readOnly"
                ></star-rating>
              </div>
              <div v-if="serviceData.ratingSummary.raterCount > 0">
                <p>
                  Rated by {{ serviceData.ratingSummary.raterCount }} people.
                  Average rating: {{ serviceData.ratingSummary.ratingAverage }} .
                </p>
              </div>
            </div>
                      <div
              v-if="userData.ownsService"
              class="mt-1 py-3  text-center"
            >
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <base-button @click="GoToServiceEdit()" type="warning"
                    >Edit Service</base-button
                  >
                  <base-button
                    v-if="
                      serviceData.datePassed && serviceData.status === 'ONGOING'
                    "
                    @click="ConfirmServiceOverCreator"
                    type="success"
                    >Service Is Over?</base-button
                  >
                </div>
              </div>
            </div>

            <div
              v-if="
                userData.attendsService &&
                serviceData.datePassed &&
                serviceData.status === 'APPROVED'
              "
              class="mt-2 py-5 text-center"
            >
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <base-button
                    @click="ConfirmServiceOverAttendee"
                    type="success"
                    >Service Is Over?</base-button
                  >
                </div>
              </div>
            </div>
            <div class="mt-2 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>{{ serviceData.description }}</p>

                  <div>
                    <badge
                      v-for="(tag, index) in serviceData.serviceTags"
                      :key="index"
                      v-bind:type="GetClass(index)"
                      rounded
                      >{{ tag.name }}</badge
                    >
                  </div>
                </div>
              </div>
            </div>

        <!-- physical -->
              <div style="margin-bottom:100px" class="text-center" v-if="serviceData.locationType === 'Physical'">
                <base-button
                  v-if="serviceData.formattedAddress != ''"
                  type="secondary"
                >
                  <GmapMap
                    :center="coordinates"
                    :zoom="13"
                    map-type-id="roadmap"
                    style="width: 500px; height: 300px"
                    ref="mapRef"
                  >
                    <GmapMarker :position="coordinates" />
                  </GmapMap>
                </base-button>
              </div>

     
              <!-- online -->

            <div
              v-if="!userData.ownsService && !userIsAdmin"
              class="mt-2 py-5 border-top text-center"
            >
              <base-button block type="primary" class="mb-3" @click="Flag()">
                Flag Service
              </base-button>
            </div>
            <div
              v-if="userIsAdmin"
              class="mt-2 py-5 border-top text-center"
            >
              <base-button block type="primary" class="mb-3" @click="DismissFlags()">
                Dismiss Flags
              </base-button>
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
import modal from "../utils/modal";
import swal from "sweetalert2";
import StarRating from "vue-star-rating";
import register from "../api/register";

export default {
  components: { BaseButton, StarRating },
  data() {
    return {
      serviceData: {
        location: "",
        locationType: "",
        time: "",
        timeString: "",
        header: "",
        minutes: "",
        description: "",
        quota: "",
        attendingUserCount: "",
        createdUserIdId: "",
        createdUserName: "",
        serviceTags: [],
        status: "",
        datePassed: false,
        participantUserList: [],
        ratingSummary: {},
        flagCount: 0,
        imageUrl: ""
      },
      userData: {
        hasServiceRequest: "",
        ownsService: "",
        attendsService: false,
      },
      ratingData:{
        readOnly : false
      },
      coordinates: {
        lat: 0,
        lng: 0,
      },
      userIsAdmin: false
    };
  },
  mounted() {
    this.GetService();
    this.GetUserDetails();

    apiRegister.GetProfile().then(r => {
        var compare = r.userType.localeCompare("ADMIN");
        this.userIsAdmin = compare == 0;
      })
  },
  computed: {},
  methods: {
    GetService() {
      var id = this.$route.params.service_id;
      apiRegister.GetService(id).then((r) => {
        this.serviceData.location = r.location;
        this.serviceData.time = r.time;
        this.serviceData.timeString = r.timeString;
        this.serviceData.header = r.header;
        this.serviceData.minutes = r.minutes;
        this.serviceData.description = r.description;
        this.serviceData.quota = r.quota;
        this.serviceData.createdUserIdId = r.createdUserIdId;
        this.serviceData.createdUserName = r.createdUserName;
        this.serviceData.serviceTags = r.serviceTags;
        this.serviceData.attendingUserCount = r.attendingUserCount;
        this.serviceData.status = r.status;
        this.serviceData.participantUserList = r.participantUserList;
        this.serviceData.datePassed = r.showServiceOverButton;
        this.coordinates.lat = r.latitude;
        this.coordinates.lng = r.longitude;
        this.serviceData.ratingSummary = r.ratingSummary;
        this.serviceData.flagCount = r.flagCount;
        this.serviceData.imageUrl = r.imageUrl;
      });
    },
    GetUserDetails() {
      var id = this.$route.params.service_id;
      apiRegister.GetUserServiceDetails(id).then((r) => {
        this.userData.hasServiceRequest = r.hasServiceRequest;
        this.userData.ownsService = r.ownsService;
        this.userData.attendsService = r.attendsService;
      });
    },
    GetClass(index) {
      var i = index + (1 % 3);
      if (i == 1) {
        return "primary";
      } else if (i == 2) {
        return "success";
      } else {
        return "warning";
      }
    },
    ConfirmRequest() {
      modal.confirm(
        "Send Request to Join?",
        "You will be added to the pending request list",
        this.SendRequest
      );
    },
    SendRequest() {
      var serviceId = this.$route.params.service_id;

      apiRegister.SendUserServiceApproval(serviceId).then((r) => {
        location.reload();
      });
    },
    Flag() {
      var serviceId = this.$route.params.service_id;

      apiRegister.FlagService(serviceId).then((r) => {
        swal.fire({
          text: "You successfully flagged the service",
        });
      });
    },
    DismissFlags() {
      var serviceId = this.$route.params.service_id;

      apiRegister.DismissFlagsForService(serviceId).then((r) => {
        swal.fire( {
          text: "You've dismissed all flags for this service."
        });
        location.reload();     

      });
      location.reload();     
    },
    ConfirmServiceOverCreator() {
      modal.confirm(
        "Do you accept that the service is over?",
        "A notification will be sent to attendees to request their approval for system completion",

        this.SendServiceOverApprovalForCreator
      );
    },
    SendServiceOverApprovalForCreator() {
      var serviceId = this.$route.params.service_id;
      apiRegister.SendServiceOverApprovalForCreator(serviceId).then((r) => {
        location.reload();
      });
    },
    ConfirmServiceOverAttendee() {
      modal.confirm(
        "Do you accept that the service is over?",
        "A notification will be sent to the creator and the service will be complete. You can rate the service after it is over.",

        this.SendServiceOverApprovalForAttendee
      );
    },
    SendServiceOverApprovalForAttendee() {
      var serviceId = this.$route.params.service_id;
      apiRegister.SendServiceOverApprovalForAttendee(serviceId).then((r) => {
        location.reload();
      });
    },
    OpenParticipantModal() {
      //when clicked on the participant count, a modal shows the list of participants to the user
      var htmlText = "";
      var i = 0;
      for (i = 0; i < this.serviceData.participantUserList.length; i++) {
        var text = "<hr>";
        var username = this.serviceData.participantUserList[i].username;
        var id = this.serviceData.participantUserList[i].id;
        text +=
          "<p><a target='_blank' href='#/profile/" +
          id +
          "'>" +
          username +
          "</a></p>";
        htmlText += text;
      }

      swal.fire({
        title: "<strong>Who is going?</strong>",
        icon: "question",
        html: htmlText,
        showCloseButton: true,
      });
    },
    GetTagInfo(tag) {
      register.GetTagInfo(tag).then((r) => {
        swal.fire({
          text: r,
        });
      });
    },
    SetRating: function (rating) {
      var id = this.$route.params.service_id;
      apiRegister.RateService(id, rating).then((r) => {
        this.ratingData.readOnly = true;
        // swal.fire({
        //   position: "top-end",
        //   icon: "success",
        //   title: "Your rating has been saved",
        //   showConfirmButton: false,
        //   timer: 1500,
        // });
      });
    },
    GoToServiceEdit() {
      var serviceId = this.$route.params.service_id;
      var url = "#/service/edit/" + serviceId;
      window.location.href = url;
    },
  },
};
</script>
<style>
</style>
