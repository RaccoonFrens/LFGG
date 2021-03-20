Original App Design Project - README Template
===

# LFGG

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)
1. [Sprints](#Sprints)

## Overview
### Description
LFGG, looking for game group is an app to find people to play games together

### App Evaluation
- **Category:** Social
- **Mobile:** Real-time updates and push notifications. Mobility to play and connect with easy access.
- **Story:** Allows users to find teammates
- **Market:** Anyone who plays online multiplayer games can make use of this app. 
- **Habit:** Users can post every time they play a new/different game or whenever their friends are not online.
- **Scope:** The basic functionality can be simply implemented consisting of querying posts and connecting them to users. Further development can be made to improve in-app messaging and UI customizations based on video games.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

 * [x] Make a public post
 * [x] Edit/Delete Post
    * [x] Have the post expire after x time
 * [x] Display other posts from a server
 * [x] Respond/message poster
 * [x] Tag of game
    * [x] Filter by tag
 * [x] Time of post (or relative time of post)

**Optional Nice-to-have Stories**

 * [x] Register
    * [x] Login
    * [x] Logout
    * [x] User profile
 * [x] Push-notifications 
 * [x] Nice UI
 * [x] Show current game status of poster
 * [x] Sort priority posts to the top
 * [x] Show in game details for popular games (those with API)
 * [x] Update profile/account settings
 * [ ] Search (for post details, profile)
 * [x] Friends
 * [ ] Stat-tracking lobbies formed
 * [ ] Achievements (IE raid leader) 

### 2. Screen Archetypes

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

### 3. Navigation

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

## Wireframes
<img src="https://github.com/TheRaccoonFrens/LFGG/blob/main/Wireframe%20V1.jpg?raw=true" width=600>

## Schema 
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
  - (Read/GET) Query all active posts<br/><br/>
DatabaseReference ref = database.getReference("server/posts");
ref.addValueEventListener(new ValueEventListener() {
  @Override
  public void onDataChange(DataSnapshot dataSnapshot) {
    Post post = dataSnapshot.getValue(Post.class);
    //TODO: add posts to UI
  }

  @Override
  public void onCancelled(DatabaseError databaseError) {
    System.out.println("The read failed: " + databaseError.getCode());
  }
});
- View Post Detail Screen
  - (Read/GET) Query replies to post<br/><br/>
DatabaseReference ref = database.getReference("server/posts/postID/reply");
ref.addValueEventListener(new ValueEventListener() {
  @Override
  public void onDataChange(DataSnapshot dataSnapshot) {
    Reply reply = dataSnapshot.getValue(Reply.class);
    //TODO: add replies to UI
  }

  @Override
  public void onCancelled(DatabaseError databaseError) {
    System.out.println("The read failed: " + databaseError.getCode());
  }
});
  - (Create/POST) Create a reply<br/><br/>
DatabaseReference ref = ref.child(String.format(url, postId);
Map<String, String> reply = new HashMap<>();
reply.put(userId, message);
ref.setValueAsync(reply);
- New Post Screen
  - (Create/POST) Create a post<br/><br/>
DatabaseReference ref = ref.child("posts");
Map<String, Post> posts = new HashMap<>();
posts.put(postID, post);
ref.setValueAsync(posts);
- Edit Post Screen
  - (Delete/DELETE) Delete a post<br/><br/>
DatabaseReference ref = ref.child(String.format(url, postId));
ref.remove();
  - (Update/PUT) Update a post<br/><br/>
DatabaseReference ref = ref.child("posts"));
Map<String, Object> update = new HashMap<>();
update.put(postId, post);
ref.updateChildrenAsync(update);

- Profile Screen
  - (Create/POST) Create bio<br/><br/>
DatabaseReference ref = ref.child("bio");
Map<String, String> bio = new HashMap<>();
posts.put(userId, bioText);
ref.setValueAsync(bio);
  - (Update/PUT) Update bio<br/><br/>
DatabaseReference ref = ref.child(String.format(url, userId));
Map<String, String> update = new HashMap<>();
update.put(userId, bioText);
ref.updateChildrenAsync(update);
  - (Update/PUT) Update top 3 games<br/><br/>
DatabaseReference ref = ref.child(String.format(url, userId));
Map<String, ArrayList<String>> update = new HashMap<>();
update.put("games", gameList);
ref.updateChildrenAsync(update);
  - (Update/PUT) change email<br/><br/>
user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email updated successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{ 
  Toast.makeText("Email already in use.",
                            Toast.LENGTH_SHORT).show();
                        }
                    }
                });
  - (Update/PUT) change password<br/><br/>
  user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "password updated.",
                                    Toast.LENGTH_SHORT).show();
                        }
else{
                            Toast.makeText(ProfileActivity.this, "failed to update password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
   
## Sprints

### Sprint #1
<img src="https://github.com/TheRaccoonFrens/LFGG/blob/main/LFGGSprint1.gif" width=250><br>
### Sprint #2
<img src="https://github.com/TheRaccoonFrens/LFGG/blob/main/sprint2.gif" width=250><br>
### Sprint #3
<img src="https://github.com/TheRaccoonFrens/LFGG/blob/main/LFGGSprint3.gif" width=250><br>

#Credits: 
This project was made possible by the incredibly helpful TechFellows at CodePath, thank you all for guiding us through our rough patches of development.

A special thank you to @Kuromippy for providing the animated splash icon! It really ties the app together :)

RaccoonFrens Team: Josh, Fred, Victor
