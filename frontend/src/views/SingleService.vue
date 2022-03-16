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
                    <a href="#"><span class="heading">{{
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
              <div class="text-center">
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
              <br />
              <div>
                <!-- <i class="ni ni-square-pin"></i> : {{ serviceData.location }} -->
                <i class="ni ni-time-alarm"></i>: {{ serviceData.timeString }}
              </div>
              <!-- <div>
                <i class="ni ni-single-02"></i>: {{ serviceData.quota }} people
              </div> -->
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
            <div class="mt-2 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>
                    Want to get more information about the tags? Click on the
                    tag that you would like to get more information about.
                  </p>

                  <div>
                    <base-button
                      block
                      type="primary"
                      class="mb-3"
                      v-for="(tag, index) in serviceData.serviceTags"
                      :key="index"
                      @click="GetTagInfo(tag.name)"
                    >
                      {{ tag.name }}
                    </base-button>
                  </div>
                </div>
              </div>
            </div>
            <div
              v-if="
                userData.ownsService &&
                serviceData.datePassed &&
                serviceData.status === 'ONGOING'
              "
              class="mt-2 py-5 border-top text-center"
            >
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <base-button @click="ConfirmServiceOverCreator" type="success"
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
              class="mt-2 py-5 border-top text-center"
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
          </div>
        </card>
      </div>
    </section>
  </div>
</template>
<script>
import BaseButton from "../../assets/components/BaseButton.vue";
import apiRegister from "../api/register";
import modal from "../utils/modal";
import swal from "sweetalert2";
import register from "../api/register";

export default {
  components: { BaseButton },
  data() {
    return {
      serviceData: {
        location: "",
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
      },
      userData: {
        hasServiceRequest: "",
        ownsService: "",
        attendsService: false,
      },
      coordinates: {
        lat: 0,
        lng: 0,
      },
    };
  },
  mounted() {
    this.GetService();
    this.GetUserDetails();
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
    ConfirmServiceOverCreator() {
      modal.confirm(
        "Do you accept that the service is over?",
        // "The participants' and your balance will be updated",
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
        // "The participants' and your balance will be updated",
        "A notification will be sent to the creator and the service will be complete",

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
        var text = "<hr>"
        var username = this.serviceData.participantUserList[i].username;
        var id = this.serviceData.participantUserList[i].id;
        text += "<p><a target='_blank' href='#/profile/"+ id+"'>"+ username +"</a></p>";        
        htmlText += text;        
      }

      swal.fire({
        title: "<strong>Who is going?</strong>",
        icon: 'question',
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
  },
};
</script>
<style>
</style>
