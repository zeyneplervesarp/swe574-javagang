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
                    disabled
                    v-if="serviceData.status === 'CANCELLED'"
                    type="danger"
                    size="sm"
                    >Cancelled Service</base-button
                  >
                  <base-button
                    v-if="
                      !userData.hasServiceRequest &&
                      !userData.ownsService &&
                      !serviceData.showServiceButton &&
                      !serviceData.datePassed
                    "
                    @click="ConfirmRequest"
                    type="info"
                    size="sm"
                    >Send Request</base-button
                  >
                  <base-button
                    disabled
                    v-if="userData.hasServiceRequest && !userData.ownsService"
                    type="info"
                    size="sm"
                    >Already requested</base-button
                  >

                  <base-button
                    disabled
                    v-if="
                      serviceData.showServiceButton && !userData.ownsService
                    "
                    type="warning"
                    size="sm"
                    >This service has passed</base-button
                  >

                  <base-button
                    size="sm"
                    @click="ConfirmServiceOverAttendee"
                    v-if="
                      userData.attendsService &&
                      !userData.ownsService &&
                      serviceData.datePassed &&
                      serviceData.status === 'APPROVED'
                    "
                    type="success"
                    v-b-popover.hover.top="
                      'Click to confirm the service is over'
                    "
                    title="Service Is Over?"
                  >
                    <i class="fa fa-check-circle-o"></i>
                  </base-button>

                  <base-button
                    size="sm"
                    @click="ConfirmServiceOverCreator"
                    v-if="
                      userData.ownsService &&
                      serviceData.datePassed &&
                      serviceData.status === 'ONGOING'
                    "
                    type="success"
                    v-b-popover.hover.top="
                      'Click to confirm the service is over'
                    "
                    title="Service Is Over?"
                  >
                    <i class="fa fa-check-circle-o"></i>
                  </base-button>

                  <base-button
                    size="sm"
                    @click="Flag()"
                    v-if="!userData.ownsService && !userIsAdmin"
                    type="warning"
                    v-b-popover.hover.top="
                      'Flag the service to notify admins of an inappropriate or illegal content.'
                    "
                    title="Flag this service"
                  >
                    <i class="fa fa-flag"></i>
                  </base-button>

                  <base-button
                    size="sm"
                    @click="DismissFlags()"
                    v-if="userIsAdmin"
                    type="default"
                    v-b-popover.hover.top="'Click to dismiss all flags'"
                    title="Dismiss Flags"
                  >
                    <i class="fa fa-flag-checkered"></i>
                  </base-button>
                  <base-button
                    size="sm"
                    v-if="
                      serviceData.status != 'CANCELLED' && userData.ownsService
                    "
                    v-b-popover.hover.top="'Click to edit this service.'"
                    title="Edit This Service"
                    @click="GoToServiceEdit()"
                    type="info"
                  >
                    <i class="fa fa-pencil-square"></i>
                  </base-button>
                  <base-button
                    size="sm"
                    @click="CancelService()"
                    v-if="
                      serviceData.status != 'CANCELLED' && userData.ownsService
                    "
                    type="danger"
                    v-b-popover.hover.top="'Click to cancel this service.'"
                    title="Cancel the Service"
                  >
                    <i class="fa fa-ban"></i>
                  </base-button>
                  <base-button
                    size="sm"
                    @click="DeleteService()"
                    v-if="userIsAdmin"
                    type="danger"
                    v-b-popover.hover.top="'Click to delete this service.'"
                    title="Delete the Service"
                  >
                    <i class="fa fa-trash"></i>
                  </base-button>
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
                    <span class="heading">{{ serviceData.hours }}</span>
                    <span class="description">Credits</span>
                  </div>
                  <div v-if="userIsAdmin">
                    <span class="heading">{{ serviceData.flagCount }}</span>
                    <span class="description">FlagCount</span>
                  </div>
                </div>
              </div>
            </div>
            <base-button
                    disabled
                    block type="danger" class="mb-3"
                    v-if="IsDateBeforeToday(serviceData.time)"
                    >Past Service
              </base-button>
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
              <div>
                <i class="ni ni-time-alarm"></i>: {{ serviceData.timeString }}
              </div>
              <div
                v-if="
                  userData.attendsService && serviceData.status === 'COMPLETED'
                "
                class="row justify-content-center"
              >
                <star-rating
                  :star-size="20"
                  @rating-selected="SetRating"
                  :read-only="ratingData.readOnly"
                ></star-rating>
              </div>
              <div
                v-if="serviceData.ratingSummary.raterCount > 0"
                class="row justify-content-center"
              >
                Rated
                <star-rating
                  :star-size="20"
                  :inline="true"
                  :rating="serviceData.ratingSummary.ratingAverage"
                  :round-start-rating="false"
                  :show-rating="false"
                  read-only
                ></star-rating>
                by {{ serviceData.ratingSummary.raterCount }} people.
              </div>
              <div class="text-center" v-if="serviceData.imageUrl !== ''">
                <base-button type="secondary">
                  <img v-bind:src="serviceData.imageUrl" />
                </base-button>
              </div>

              <div class="text-center">
                <p>Location: {{ serviceData.location }}</p>
              </div>
              <br />
            </div>

            <!-- physical -->
            <div
              style="margin-bottom: 30px"
              class="text-center"
              v-if="serviceData.locationType === 'Physical'"
            >
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
          </div>
          <div class="py-4 border-top text-center">
            <div class="row justify-content-center">
              <div class="col-lg-9">
                <p>{{ serviceData.description }}</p>
                <div>
                  <div>
                    <base-badge-button
                      v-for="(tag, index) in serviceData.serviceTags"
                      :key="index"
                      v-bind:type="GetClass(index)"
                      rounded
                      @click="GetTagInfo(tag.name)"
                      >{{ tag.name }}
                    </base-badge-button>
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
import modal from "../utils/modal";
import swal from "sweetalert2";
import StarRating from "vue-star-rating";
import register from "../api/register";
import { VBTooltip } from "bootstrap-vue/esm/directives/tooltip/tooltip";
import { VBPopover } from "bootstrap-vue/esm/directives/popover/popover";
import BaseBadgeButton from "../components/BaseBadgeButton.vue";

export default {
  components: { BaseButton, StarRating, BaseBadgeButton },
  directives: {
    BTooltip: VBTooltip,
    BPopover: VBPopover,
  },
  data() {
    return {
      serviceData: {
        location: "",
        locationType: "",
        time: "",
        timeString: "",
        header: "",
        hours: "",
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
        imageUrl: "",
      },
      userData: {
        hasServiceRequest: "",
        ownsService: "",
        attendsService: false,
      },
      ratingData: {
        readOnly: false,
      },
      coordinates: {
        lat: 0,
        lng: 0,
      },
      userIsAdmin: false,
    };
  },
  mounted() {
    this.GetService();
    this.GetUserDetails();

    apiRegister.GetProfile().then((r) => {
      var compare = r.userType.localeCompare("ADMIN");
      this.userIsAdmin = compare == 0;
    });
  },
  computed: {},
  methods: {
    IsDateBeforeToday(date) {
      var today = new Date();
      var serviceDate = new Date(date);
      return today > serviceDate;
    },
    IsCancellationDatePassed() {
      var date = new Date();
      if (this.serviceData.locationType === "Physical") {
        date = date.setTime(date.getTime() + 24 * 60 * 60 * 1000);
      } else {
        date = date.setTime(date.getTime() + 0.5 * 60 * 60 * 1000);
      }
      return Date.parse(this.serviceData.time) < date;
    },
    GetService() {
      var id = this.$route.params.service_id;
      apiRegister.GetService(id).then((r) => {
        this.serviceData.location = r.location;
        this.serviceData.time = r.time;
        this.serviceData.timeString = r.timeString;
        this.serviceData.header = r.header;
        this.serviceData.hours = r.hours;
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
        this.serviceData.locationType = r.locationType;
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
      console.log("SendRequest triggered");
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
      console.log("DismissFlags");
      apiRegister.DismissFlagsForService(serviceId).then((r) => {
        swal.fire({
          text: "You've dismissed all flags for this service.",
        });
        this.serviceData.flagCount = 0;
      });
    },
    ConfirmServiceOverCreator() {
      modal.confirm(
        "Do you accept that the service is over?",
        "A notification will be sent to attendees to request their approval for system completion",

        this.SendServiceOverApprovalForCreator
      );
    },
    CancelService() {
      if (this.IsCancellationDatePassed()) {
        swal
          .fire({
            title:
              "Do you want to cancel this service? You will lose 5 reputation points because of the cancellation deadline.",
            showCancelButton: true,
            confirmButtonText: "Yes",
          })
          .then((result) => {
            if (result.isConfirmed) {
              var serviceId = this.$route.params.service_id;
              apiRegister.CancelService(serviceId).then((r) => {
                swal.fire({
                  text: "You've cancelled this service",
                });
              });
              location.reload();
            }
          });
      } else {
        var serviceId = this.$route.params.service_id;
        apiRegister.CancelService(serviceId).then((r) => {
          location.reload();
        });
      }
    },
    DeleteService() {
      swal
        .fire({
          title: "Do you want to delete this service?",
          showCancelButton: true,
          confirmButtonText: "Yes",
        })
        .then((result) => {
          if (result.isConfirmed) {
            var serviceId = this.$route.params.service_id;
            apiRegister.DeleteService(serviceId).then((r) => {
              swal
                .fire({
                  text: "You've deleted this service",
                  showCancelButton: false,
                  confirmButtonText: "Back to Services",
                })
                .then((result) => {
                  /* Read more about isConfirmed, isDenied below */
                  if (result.isConfirmed) {
                    window.location.href = "#/allServices";
                  }
                });
            });
          }
        });
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
