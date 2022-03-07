const webpack = require('webpack');
const path = require("path");
const vueSrc = "./src";

module.exports = {
  devServer: {
    disableHostCheck: true,    
    headers: {
      'Access-Control-Allow-Private-Network': true,
      'Access-Control-Allow-Origin': '*',
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        "@": path.resolve(__dirname, vueSrc)
      },
      
    },

    // alias:{
    //   '@': path.resolve('src')
    // },
    plugins: [
      new webpack.optimize.LimitChunkCountPlugin({
        maxChunks: 6
      })
    ]
  },
  pwa: {
    name: 'SocialHub',
    themeColor: '#172b4d',
    msTileColor: '#172b4d',
    appleMobileWebAppCapable: 'yes',
    appleMobileWebAppStatusBarStyle: '#172b4d'
  },
  css: {
    // Enable CSS source maps.
    sourceMap: process.env.NODE_ENV !== 'production'
  }
};
