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
 <img src='Wireframe V1.jpg' title='Wireframe V1' width='600px' alt='Wireframe' />
 
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

## 4. Schema

### Models

#### Post
| Property     | Type   | Description                | 
| ----         | -----  | -----                      |
| objectId     | String | unique id for the user post|
| author       | User reference | author of the post      |
| body         | String | post text |
| tag          | String | game tag |
| duration     | Date | expiration time |
| size         | int | party size |
| thread       | Array of comment references | replies to post |

#### User
| Property     | Type   | Description                | 
| ----         | -----  | -----                      |
| userId | String | unique id for the user |
| username | String | username | 
| email | String | user email |
| posts | Array of references | posts created by user |

#### Comment
| Property     | Type   | Description                | 
| ----         | -----  | -----                      |
| objectId | String | unique id for comment |
| author | User reference | author of comment |
| body | String | comment text |

### Networking
- Home Screen
  - (Read/GET) Query all active posts
- View Post Detail Screen
  - (Read/GET) Query replies to post
  - (Create/POST) Create a reply
- New Post Screen
  - (Create/POST) Create a post
- Edit Post Screen
  - (Delete/DELETE) Delete a post
  - (Update/PUT) Update a post
- Profile Screen
  - (Create/POST) Create bio
  - (Update/PUT) Update bio
  - (Update/PUT) Update top 3 games
  - (Update/PUT) change username
  - (Update/PUT) change email     
