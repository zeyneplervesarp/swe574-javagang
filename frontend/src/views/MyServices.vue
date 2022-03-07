<template>
  <div>
    <div class="position-relative">
      <section class="section section section-shaped my-0 overflow-hidden">
        <div class="shape shape-style-1 bg-gradient-warning shape-skew">
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
                <img src="img/ill/ill-2.svg" class="img-center img-fluid" />
              </div>
            </div>
            <div class="col-lg-6 order-lg-1">
              <div class="d-flex px-3">
                <div>
                  <icon
                    name="ni ni-building"
                    size="lg"
                    class="bg-gradient-white"
                    color="primary"
                    shadow
                    rounded
                  ></icon>
                </div>
                <div class="pl-4">
                  <h4 class="display-3 text-white">Your Services</h4>
                  <p class="text-white">
                    Here you can find the services that you have created so far,
                    you can also list the services of others.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
    
    <section class="section section-lg pt-lg-0 mt--400 ">
        <!-- <services :getByUser=true></services> -->
        <services :filter="'createdByUser'"></services>

    </section>

    <section class="section section-lg pt-0">
      <div class="container">
        <card gradient="warning" no-body shadow-size="lg" class="border-0">
          <div class="p-5">
            <div class="row align-items-center">
              <div class="col-lg-8">
                <h3 class="text-white">
                  Want to take a look at others' services?
                </h3>
                <p class="lead text-white mt-3">
                  Socialhub is a fair community. You can always take part in other services.
                </p>
              </div>
              <div class="col-lg-3 ml-lg-auto">
                <base-button
                  tag="a"
                  href="#/services"
                  type="white"
                  block
                  size="lg"
                >
                  Click to Browse
                </base-button>
              </div>
            </div>
          </div>
        </card>
      </div>
    </section>

  </div>

</template>

<script>
import Services from "./components/Services.vue";

export default {
  components: {
    Services,
  },
};

</script>
