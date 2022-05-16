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
                  Feed           
              </h3>
              <br />

              <div class="row justify-content-center">
              <div class="row pull-left">
                <base-dropdown>
                  <base-button
                    slot="title"
                    type="warning"
                    class="dropdown-toggle float-right"
                  >
                    Filter By
                  </base-button>
                  <a class="dropdown-item" href="#" v-on:click="GetFeed()"
                    >Show All
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
                  ></a>
                  <a class="dropdown-item" href="#" v-on:click="GetFilteredFeed('user_login_successful')"
                    >Successful Login
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
                  ></a>
                  <a class="dropdown-item" href="#" v-on:click="GetFilteredFeed('user_login_failed')"
                    >Unsuccessful Login
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-desc mr-2"></i>
                    </span>
                  </a>
                  <a
                    class="dropdown-item"
                    href="#"
                    v-on:click="GetFilteredFeed('service_created')"
                    >Created Service
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
                  ></a>
                  <a
                    class="dropdown-item"
                    href="#"
                    v-on:click="GetFilteredFeed('service_join_requested')"
                    >Service Join Request
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-desc mr-2"></i> </span
                  ></a>
                  <a
                    class="dropdown-item"
                    href="#"
                    v-on:click="GetFilteredFeed('service_join_approved')"
                    >Service Join Approval
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
                  ></a>
                  <a
                    class="dropdown-item"
                    href="#"
                    v-on:click="GetFilteredFeed('follow')"
                    >Follow
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-desc mr-2"></i> </span
                  ></a>
                </base-dropdown>
              </div>
              </div>


               <div class="container ct-example-row">
                      <div
                        class="row"
                        v-for="(result, index) in feed"
                        :key="index"
                      >
                        <div class="col mt-2 text-center">
                        <span
                            v-if="
                              result.verb == 'follow'
                            "
                          >
                            <base-button
                              @click="GoToUrl(result.object.url)"
                              block
                              type="default"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}
                              <badge class="float-right" pill type="default">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span
                            v-if="result.verb == 'login'">
                            <base-button
                              @click="GoToUrl(result.actor.url)"
                              block
                              type="success"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}
                              <badge class="float-right" pill type="success">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span
                            v-if="result.verb == 'join-request'">
                            <base-button
                              @click="GoToUrl(result.object.url)"
                              block
                              type="info"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}
                              <badge class="float-right" pill type="info">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span
                            v-if="
                              result.verb == 'create'"
                          >
                            <base-button
                              @click="GoToUrl(result.object.url)"
                              block
                              type="primary"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}
                              <badge class="float-right" pill type="primary">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span
                            v-if="
                              result.verb == 'approve'
                            "
                          >
                            <base-button
                              @click="GoToUrl(result.actor.url)"
                              block
                              type="secondary"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}
                              <badge class="float-right" pill type="secondary">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span v-if="result.verb == 'login-failed'">
                            <base-button
                              @click="GoToUrl(result.target.url)"
                              block
                              type="danger"
                              >{{ GetFormattedDate(result.published) }} || {{ result.summary }}

                              <badge class="float-right" pill type="danger">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                        </div>
                        <div class="w-100"></div>
                      </div>
                    <infinite-loading
                      @infinite="infiniteHandler"
                      spinner="spiral"
                    ></infinite-loading>
              <br />
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
import infiniteLoading from "vue-infinite-loading";
import BackToTop from 'vue-backtotop'
import moment from 'moment';
import BaseDropdown from "@/components/BaseDropdown";

export default {
  components: {
    infiniteLoading,
    BackToTop,
    BaseDropdown,
  },
  data() {
    return {
      feed: [],
      next: "",
      filter_key: "",
    };
  },
  mounted() {
    this.GetFeed();
  },
  methods: {
    GetFormattedDate(date) {
      return moment(date).format("YYYY-MM-DD")
    },
    GetFeed() {
      apiRegister.GetAdminFeed().then((r) => {
        this.feed = r.items;
        this.next = r.next;
      });
    },
    GetFilteredFeed(key) {
      apiRegister.GetAdminFeed(this.next, key).then((r) => {
        this.feed = r.items;
        this.next =  r.next;
        this.filter_key = r.filterKey;
      })
    },
    infiniteHandler($state) {
      apiRegister.GetAdminFeed(this.next, this.filter_key).then((r) => {
        if (r.items.length) {
          setTimeout(() => {
            this.next = r.next;
            this.filter_key = r.filterKey;
            var merged = [...this.feed, ...r.items];
            this.feed = merged;
            $state.loaded();
          }, 1000);
        } else {
          $state.complete();
        }
      });
    },
    GoToUrl(url) {
      url = url.replace("user", "profile");
      url = "#" + url;
      window.location.href = url;
    },
  },
};
</script>
<style>
</style>
