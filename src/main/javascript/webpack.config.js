var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: "./src/index.tsx",
  output: {
    filename: "bundle.js",
    path: path.resolve('../resources/webroot/assets/')
  },
  watch: true,
  optimization: {},
  devServer: {
    contentBase: path.join(__dirname, '/dist'),
    compress: true,
    port: 9000,
    watchContentBase: true
  },
  // Enable sourcemaps for debugging webpack's output.
  devtool: "source-map",

  resolve: {
    // Add '.ts' and '.tsx' as resolvable extensions.
    extensions: [".ts", ".tsx", ".js", ".json"],
  },

  module: {
    rules: [
      // All files with a '.ts' or '.tsx' extension will be handled by 'awesome-typescript-loader'.
      {test: /\.tsx?$/, loader: "awesome-typescript-loader"},
      {
        test: /\.scss$/,
        use: [
          "style-loader", // creates style nodes from JS strings
          "css-loader", // translates CSS into CommonJS
          "sass-loader" // compiles Sass to CSS, using Node Sass by default
        ]
      },
      // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
      {enforce: "pre", test: /\.js$/, loader: "source-map-loader"}
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'src/index.html',
      filename: 'index.html'
    })
  ]
  // When importing a module whose path matches one of the following, just
  // assume a corresponding global variable exists and use that instead.
  // This is important because it allows us to avoid bundling all of our
  // dependencies, which allows browsers to cache those libraries between builds.
  // externals: {
  //     "react": "React",
  //     "react-dom": "ReactDOM"
  // }
};
