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
                    v-if="!isOwnProfile && !alreadyFollowing"
                    v-on:click="FollowUser()"
                    type="info"
                    size="sm"
                    >Follow</base-button
                  >
                  <base-button
                    v-if="!isOwnProfile && alreadyFollowing"
                    type="success"
                    size="sm"
                    >Already Following</base-button
                  >

                  <base-button
                    size="sm"
                    @click="Flag()"
                    :disabled="flagButtonDisabled"
                    v-if="!userIsAdmin && !isOwnProfile"
                    type="warning"
                    v-b-popover.hover.top="
                      'Flag the user to notify admins of an inappropriate or illegal content.'
                    "
                    title="Flag this user"
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
                    @click="DeleteUser(userData.id)"
                    v-if="userIsAdmin && !isOwnProfile"
                    type="danger"
                    v-b-popover.hover.top="'Click to delete this user.'"
                    title="Delete the User"
                  >
                    <i class="fa fa-trash"></i>
                  </base-button>
                </div>
              </div>
              <div class="col-lg-4 order-lg-1">
                <div class="card-profile-stats d-flex justify-content-left">
                  <div @click="OpenFollowingModal()">
                    <a href="#">
                      <span class="heading">{{
                        userData.following.length
                      }}</span>
                      <span class="description">Following</span>
                    </a>
                  </div>
                  <div @click="OpenFollowerModal()">
                    <a href="#">
                      <span class="heading">{{
                        userData.followedBy.length
                      }}</span>
                      <span class="description">Followers</span>
                    </a>
                  </div>
                  <div v-if="isOwnProfile">
                    <span class="heading">{{ userData.balance }}</span>
                    <span class="description"
                      >Credits ({{ userData.balanceOnHold }} on hold)</span
                    >
                  </div>

                  <div>
                    <span class="heading">{{ userData.reputationPoint }}</span>
                    <span class="description">Reputation Points</span>
                  </div>
                  <div v-if="userIsAdmin && !isOwnProfile">
                    <span class="heading">{{ userData.flagCount }}</span>
                    <span class="description">Flag Count</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="text-center mt-4">
              <h3>
                {{ userData.username }}
                <span class="font-weight-light"></span>
              </h3>
            </div>

            <div class="row mt-3 mb-3 justify-content-center">
              Rated
              <star-rating
                :inline="true"
                :star-size="20"
                :read-only="true"
                :show-rating="true"
                :rating="userData.ratingSummary.ratingAverage"
              ></star-rating
              >.
            </div>
            <div class="row mt-3 justify-content-center">
              <div
                v-for="(badge, index) in userData.badges"
                :key="index"
                class="column"
              >
                <img
                  v-bind:src="'img/badges/' + badge.badgeType + '.png'"
                  v-bind:alt="badge.badgeType"
                  style="max-width: 70px"
                />
              </div>
            </div>

            <div class="py-4 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>{{ userData.bio }}</p>
                  <div>
                    <base-badge-button
                      v-for="(tag, index) in userData.tags"
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
import apiRegister from "../api/register";
import swal from "sweetalert2";
import BaseButton from "../components/BaseButton.vue";
import Badge from "../components/Badge.vue";
import BaseBadgeButton from "../components/BaseBadgeButton.vue";
import StarRating from "vue-star-rating";
import { VBTooltip } from "bootstrap-vue/esm/directives/tooltip/tooltip";
import { VBPopover } from "bootstrap-vue/esm/directives/popover/popover";
export default {
  components: { BaseButton, Badge, BaseBadgeButton, StarRating },

  directives: {
    BTooltip: VBTooltip,
    BPopover: VBPopover,
  },
  data() {
    return {
      userData: {
        id: 0,
        username: "",
        email: "",
        bio: "",
        balance: 0,
        balanceOnHold: 0,
        reputationPoint: 0,
        flagCount: 0,
        following: [],
        followedBy: [],
        tags: [],
        badges: [],
        ratingSummary: {},
      },
      isOwnProfile: false,
      alreadyFollowing: false,
      userIsAdmin: false,
      flagButtonDisabled: false
    };
  },
  mounted() {
    this.GetProfile();
    this.AlreadyFollowing();

    apiRegister.GetProfile().then((r) => {
      var compare = r.userType.localeCompare("ADMIN");
      this.userIsAdmin = compare == 0;
      this.isOwnProfile =
        this.$route.params.userId == r.id || this.$route.params.userId == null;
    });
  },
  methods: {
    GetProfile() {
      var id = this.$route.params.userId;
      apiRegister.GetProfile(id).then((r) => {
        this.userData.id = r.id;
        this.userData.username = r.username;
        this.userData.email = r.email;
        this.userData.bio = r.bio;
        this.userData.balance = r.balance;
        this.userData.balanceOnHold = r.balanceOnHold;
        this.userData.reputationPoint = r.reputationPoint;
        this.userData.following = r.following;
        this.userData.followedBy = r.followedBy;
        this.userData.tags = r.tags;
        this.userData.flagCount = r.flagCount;
        this.userData.badges = r.badges;
        this.userData.ratingSummary = r.ratingSummary;
      });
    },
    Flag() {
      var userId = this.$route.params.userId;

      apiRegister.FlagUser(userId).then((r) => {
        swal.fire({
          text: "You successfully flagged the user.",
        });
        this.flagButtonDisabled = true;
      });
    },
    FormatDouble(num) {
      return Math.round(num * 10) / 10;
    },
    DismissFlags() {
      var userId = this.$route.params.userId;

      apiRegister.DismissFlagsForUser(userId).then((r) => {
        location.reload();
      });
    },
    FollowUser() {
      var id = this.$route.params.userId;
      apiRegister.FollowUser(id).then((r) => {
        this.AlreadyFollowing();
        location.reload();
      });
    },
    AlreadyFollowing() {
      var id = this.$route.params.userId;
      apiRegister.CheckIfFollowExists(id).then((r) => {
        this.alreadyFollowing = r;
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
    DeleteUser(userId) {
      swal
        .fire({
          title: "Do you want to delete this user?",
          showCancelButton: true,
          confirmButtonText: "Yes",
        })
        .then((result) => {
          if (result.isConfirmed) {
            apiRegister.DeleteUser(userId).then((r) => {
              swal
                .fire({
                  text: "You've deleted this user",
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
    GetTagInfo(tagname) {
      apiRegister.GetTagInfo(tagname).then((r) => {
        swal.fire({
          text: r,
        });
      });
    },
    OpenFollowingModal() {
      //when clicked on the participant count, a modal shows the list of participants to the user
      var htmlText = "";
      var i = 0;
      apiRegister.GetFollowingsByUserId(this.userData.id).then((r) => {
        console.log(r);
        for (i = 0; i < r.length; i++) {
          var text = "<hr>";
          var username = r[i].username;
          var id = r[i].id;
          text +=
            "<p><a target='_blank' href='#/profile/" +
            id +
            "'>" +
            username +
            "</a></p>";
          htmlText += text;
        }
      });

      setTimeout(() => {
        swal.fire({
          title: "<strong>Who am I following?</strong>",
          icon: "question",
          html: htmlText,
          showCloseButton: true,
        });
      }, 1000);
    },
    OpenFollowerModal() {
      //when clicked on the participant count, a modal shows the list of participants to the user
      var htmlText = "";
      var i = 0;
      apiRegister.GetFollowersByUserId(this.userData.id).then((r) => {
        for (i = 0; i < r.length; i++) {
          var text = "<hr>";
          var username = r[i].username;
          var id = r[i].id;
          text +=
            "<p><a target='_blank' href='#/profile/" +
            id +
            "'>" +
            username +
            "</a></p>";
          htmlText += text;
        }
      });

      setTimeout(() => {
        swal.fire({
          title: "<strong>Who is following?</strong>",
          icon: "question",
          html: htmlText,
          showCloseButton: true,
        });
      }, 1000);
    },
  },
  props: {
    userId: String,
  },
};
</script>
<style>
</style>
