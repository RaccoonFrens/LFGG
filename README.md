# GGG
Gaze Game Group

LFGG, looking for game group is an app to find people to play games together with

Your [design product spec](https://hackmd.io/s/H1wGpVUh7) (described in that link) will look like the following in your README:

## 1. User Stories (Required and Optional)

**Required Must-have Stories**

 * [ ] Make a public post
 * [ ] Edit/Delete Post
    * [ ] Have the post expire after x time
 * [ ] Display other posts from a server
 * [ ] Respond/message poster
 * [ ] Tag of game
    * [ ] Filter by tag
 * [ ] Time of post (or relative time of post)
 * [ ] Push-notifications 
 
**Optional Nice-to-have Stories**
 * [ ] Register
    * [ ] Login
    * [ ] User profile
 * [ ] Nice UI
 * [ ] Show current game status of poster
 * [ ] Sort priority posts to the top
 * [ ] Show in game details for popular games (those with API)
 * [ ] Sort priority posts to the top
 * [ ] Update profile/account settings
 * [ ] Search (for post details, profile)
 * [ ] Friends
 * [ ] Stat-tracking lobbies formed
 * [ ] Achievements (IE raid leader) 

## 2. Screen Archetypes
 * [Miro wireframe](https://miro.com/app/board/o9J_lVrT3mQ=/)
 
 * Login Screen 
   * Users can log in to access their account and post history
 * Register Screen
   * Users can register for an account to create posts
 * Profile Page
   * Shows user information such as timezones and games
 * Home
   * User can view public posts from a server
   * Allow the user to make a public post
   * User can set posts to expire after x time
   * User can set posts with game tags
   * User can parse the content of posts using a search bar 
   * User can filter posts by game
   * User can edit/delete their posts
   * Home screen will feature a card style post display
 * New post
   * Screen for users to make a public post
   * Users can decide to have the post expire after x time
   * Users can chooses the game tag of the post 
 * Settings
   * Users can update profile/account settings
 * Post details
   * Users can respond to a post publically or privately to poster
   * User can edit/delete their post
## 3. Navigation

**Tab Navigation** (Tab to Screen)

 * Home
 * Profile
 * New Post
 * Search

**Flow Navigation** (Screen to Screen)

 * Login
   * Register
   * Home
 * Register
   * Login
 * Home
   * Profile
   * New Post
   * Search
   * Post Details
 * Profile
   * Home
   * New Post
   * Search
   * Settings
 * New Post
   * Home
   * Profile
   * Search
 * Search
   * Home
   * Profile
   * New Post
   * Post Details
