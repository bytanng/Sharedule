# Step 1: Use an official Node.js image to build the app
FROM node:20 AS build

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy package.json and package-lock.json
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy the rest of the application code
COPY . .

# Step 6: Build the React app for production
RUN npm run build

# Step 7: Serve the build using a lightweight web server (e.g., Nginx or a Node server)
FROM nginx:alpine

# Step 8: Copy the build output from the previous stage to Nginx's public directory
COPY --from=build /app/build /usr/share/nginx/html

# Step 9: Expose the port Nginx is listening on (default is 80)
EXPOSE 80

# Step 10: Run Nginx in the foreground to keep the container running
CMD ["nginx", "-g", "daemon off;"]
