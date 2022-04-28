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
              <h4>User Activites</h4>
            </div>
            <div class="mt-5 py-5 border-top text-center">
              <div class="row justify-content-center">
                <div class="col-lg-12">
                  <div class="container ct-example-row">
                    <div>
                      <div
                        class="row"
                        v-for="(result, index) in feed"
                        :key="index"
                      >
                        <div class="col mt-2 text-center">
                          <span
                            v-if="
                              result.verb == 'login' ||
                              result.verb == 'join-request'
                            "
                          >
                            <base-button
                              @click="GoToUrl(result.target.url)"
                              block
                              type="info"
                              >{{ result.summary }}
                              <badge class="float-right" pill type="info">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span
                            v-if="
                              result.verb == 'create' ||
                              result.verb == 'approve'
                            "
                          >
                            <base-button
                              @click="GoToUrl(result.target.url)"
                              block
                              type="success"
                              >{{ result.summary }}
                              <badge class="float-right" pill type="success">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                          <span v-if="result.verb == 'login-failed'">
                            <base-button
                              @click="GoToUrl(result.target.url)"
                              block
                              type="info"
                              >{{ result.summary }}

                              <badge class="float-right" pill type="info">{{
                                result.verb
                              }}</badge>
                            </base-button>
                          </span>
                        </div>
                        <div class="w-100"></div>
                      </div>
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
import apiRegister from "../api/register";
import infiniteLoading from "vue-infinite-loading";
import BackToTop from 'vue-backtotop'

export default {
  components: {
    infiniteLoading,
    BackToTop,
  },
  data() {
    return {
      feed: [],
      next: "",
    };
  },
  mounted() {
    this.GetFeed();
  },
  methods: {
    GetFeed() {
      apiRegister.GetAdminFeed().then((r) => {
        this.feed = r.items;
        this.next = r.next;
      });
    },
    GoToUrl(url) {
      url = url.replace("user", "profile");
      url = "#" + url;
      window.location.href = url;
    },
    infiniteHandler($state) {
      apiRegister.GetAdminFeed(this.next).then((r) => {
        if (r.items.length) {
          setTimeout(() => {
            this.next = r.next;
            var merged = [...this.feed, ...r.items];
            this.feed = merged;
            $state.loaded();
          }, 1000);
        } else {
          $state.complete();
        }
      });
    },
  },
};
</script>
<style>
</style>
