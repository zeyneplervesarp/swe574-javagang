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
              <div class="col-lg-3 order-lg-2">
              </div>
              <div
                class="col-lg-4 order-lg-3 text-lg-right align-self-lg-center"
              >
                <div
                  v-if="!isOwnProfile && !alreadyFollowing"
                  class="card-profile-actions py-4 mt-lg-0"
                >
                  <base-button
                    v-on:click="FollowUser()"
                    type="info"
                    size="sm"
                    class="mr-4"
                    >Follow</base-button
                  >
                </div>
                <div
                  v-if="!isOwnProfile && alreadyFollowing"
                  class="card-profile-actions py-4 mt-lg-0"
                >
                  <base-button type="success" size="sm" class="mr-4"
                    >Already Following</base-button
                  >
                </div>
              </div>
              <div class="col-lg-4 order-lg-1">
                <div class="card-profile-stats d-flex justify-content-left">
                  <div>
                    <span class="heading">{{ userData.following.length }}</span>
                    <span class="description">Following</span>
                  </div>
                  <div>
                    <span class="heading">{{
                      userData.followedBy.length
                    }}</span>
                    <span class="description">Followers</span>
                  </div>
                  <div v-if="isOwnProfile">
                    <span class="heading">{{ userData.balance }}</span>
                    <span class="description">Credits</span>
                  </div>
                  <div v-if="isOwnProfile">
                    <span class="heading">{{ userData.balanceOnHold }}</span>
                    <span class="description">Credits on Hold</span>
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
            <div class="text-center mt-5">
              <h3>
                {{ userData.username }}
                <span class="font-weight-light"></span>
              </h3>
                    <badge
                    v-for="(badge, index) in userData.badges"
                    :key="index"
                    v-bind:type="GetClass(index)"
                    rounded
                    >{{ badge.badgeType }}</badge
                  >
            </div>
            <div class="mt-5 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-9">
                  <p>{{ userData.bio }}</p>
                     <div>
                  <badge
                    v-for="(tag, index) in userData.tags"
                    :key="index"
                    v-bind:type="GetClass(index)"
                    rounded
                    >{{ tag.name }}</badge
                  >
                </div>
                </div>
             
              </div>
            </div>
            <div 
              v-if="userIsAdmin && !isOwnProfile"
              class="mt-2 py-5 border-top text-center">
                <base-button
                  block
                  type="primary"
                  class="mb-3"
                  @click="DismissFlags()"
                > Dismiss Flags
                </base-button>
             </div>
            <div
            v-if="!userIsAdmin && !isOwnProfile"
            class="mt-2 py-5 border-top text-center">
              <base-button
                block
                type="primary"
                class="mb-3"
                @click="Flag()"
              > Flag User
              </base-button>
            </div>
            <div
            v-if="userIsAdmin && !isOwnProfile"
            class="mt-2 py-5 border-top text-center">
            <base-button block type="warning" @click="DeleteUser(userData.id)" class="mb-3">
              Delete User
            </base-button>
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
export default {
  components: {},
  data() {
    return {
      userData: {
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
        badges: []
      },
      isOwnProfile: this.$route.params.userId == null,
      alreadyFollowing: false,
      userIsAdmin: false
    };
  },
  mounted() {
    this.GetProfile();
    this.AlreadyFollowing();

    apiRegister.GetProfile().then(r => {
        var compare = r.userType.localeCompare("ADMIN");
        this.userIsAdmin = compare == 0;
      })
  },
  methods: {
    GetProfile() {
      var id = this.$route.params.userId;
      apiRegister.GetProfile(id).then((r) => {
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
        console.log("ok.");
      });
    },
    Flag() {
      var userId = this.$route.params.userId;

      apiRegister.FlagUser(userId).then((r) => {
        swal.fire({
        text: "You successfully flagged the user.",
        });
      });
    },
    DismissFlags() {
      var userId = this.$route.params.userId;

      apiRegister.DismissFlagsForUser(userId).then((r) => {
        swal.fire({
          text: "You dismissed all flags for this user.",
        });
      });
    },
    FollowUser() {
      var id = this.$route.params.userId;
      apiRegister.FollowUser(id).then((r) => {
        this.AlreadyFollowing();
        console.log("ok.");
      });
    },
    AlreadyFollowing() {
      var id = this.$route.params.userId;
      apiRegister.CheckIfFollowExists(id).then((r) => {
        this.alreadyFollowing = r;
        console.log("ok.");
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
    DeleteUser(userId){
        apiRegister.DeleteUser(userId).then((r)=>{
        this.$router.go();
      });
    },
  },
  props: {
    userId: String,
  },
};
</script>
<style>
</style>
