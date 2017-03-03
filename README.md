# PurpleViper

(Project description and requirements omitted to reduce the chance of someone else finding the solution via Google)

## Analysis of requirements

Overall, this problem is solvable with a multiple master data architecture with a simple REST API on top. Fortunately, there are no operations that manipulate a global shared resource (account balances, airplane seats/hotel rooms, license assignment, etc) so any conflicts will be relatively painless to resolve.

## Architecture

* Each site has a complete copy of all data.
* Each site will form a node in a peer-to-peer network.
* Each node will need to know about some (or all) of its peers in order to replicate changes.
* Data changes on one node are asychronously replicated to each of its peers.
* In the event of network segmentation, replication events are queued until connectivity is restored, then replication continues where it left off.

### Data Store

Couchbase appears to fulfill the most difficult requirement, that of cross-site data replication. According to the marketing materials (https://www.couchbase.com/binaries/content/assets/website/docs/whitepapers/extend-your-data-tier-with-xdcr.pdf), the XCDR feature will:
* Fulfill our multiple physical site needs by replicating changes on one Couchbase cluster to the other known clusters
* Fulfill our resilience needs by queuing replication events in the event that a peer isn't reachable
* Fulfill our consistency needs by automatically choosing one version of a conflicted document to "win", although our data model will decide how much is "lost" in a conflict

### Data Model (simple)

An `asset` is modeled as a single JSON document, decribed here in JSON Schema:

```json
{
	"title": "Asset",
	"type": "object",
	"properties": {
		"id": {
			"type": "string"
		},
		"uri": {
			"type": "string"
		},
		"name": {
			"type": "string"
		},
		"notes": {
			"type": "array",
			"items": { "type": { "$ref": "#/definitions/note" }
		}
	},
	"definitions": {
		"note": {
			"title": "Note",
			"type": "object",
			"properties": {
				"text": {
					"type": "string"
				}
			}		
		}
	}
}
```

### Conflict resolution

#### Simple design (as implemented)
Newest version wins. `uri`, `name`, and `notes` collection of the old asset version are lost. This design is not optimal because Notes created on one site can be completely lost due to a "newer" change to one of the Asset fields or the Notes list on another site.

#### Better design (theoretical)

There is no semantic reason why we couldn't merge the two `notes` collections, but then each Note would need to be stored as a separate document, yielding another set of engineering challenges. (How to retreive the related Notes for an Asset, orphaned assets, etc.)

## REST API

### GET /assets

Returns an array of all assets.

    $ curl GET http://localhost:8080/assets

### GET /assets/{id}

Returns a specific asset with the given id, including notes.

    $ curl http://localhost:8080/assets/b6a94d36-b6b6-4f20-a176-cfd99161e421
    > {"id":"b6a94d36-b6b6-4f20-a176-cfd99161e421","uri":"asset://media/vhs","name":"VHS Tapes","notes":[{"text":"low on stock, order more"}]}

### POST /assets

Creates a new asset with the given uri and name.


    $ curl -X POST http://localhost:8080/assets -H "Content-Type: application/json" -d '{"uri":"asset://media/vhs", "name":"VHS Tapes"}'
    > {"id":"b6a94d36-b6b6-4f20-a176-cfd99161e421","uri":"asset://media/vhs","name":"VHS Tapes","notes":nil]}

### DELETE /assets/{id}

Deletes an asset and its notes.

    $ curl -X DELETE http://localhost:8080/b6a94d36-b6b6-4f20-a176-cfd99161e421

### POST /assets/{id}/notes

Adds a note to an existing asset. Returns the created note.

    $ curl -X POST http://localhost:8080/assets/b6a94d36-b6b6-4f20-a176-cfd99161e421/notes -H "Content-Type: application/json" -d '{"text":"low on stock, order more"}'
    > {"text":"low on stock, order more"}

## Setup

### Couchbase

Couchbase is required for development and can be run locally, but since at least two instances are needed to demonstrate cross-site replication hosting in the cloud is more convienent. (For a single local installation using Docker, see https://developer.couchbase.com/documentation/server/4.6/getting-started/do-a-quick-install.html).

#### A Single Instance

An easy (but not cheap) path in AWS is to use one of the official AMIs in the Marketplace. Browse to https://aws.amazon.com/marketplace/pp/B011W4I8ZG?qid=1488550519402&sr=0-3&ref_=srh_res_product_title and go through the 1-Click Launch process to start up your instance.
* Pick the smallest instance type possible.
* Precreate a security group and SSL certificate as it will be convenient to reuse them for the second instance.
* Default usernames and passwords are used throughout this example setup, so setting a restrictive firewall is a good precaution. Couchbase uses a wide range of ports so they can either be listed out individually, or whitelist them all (1-64000).

Name the EC2 instance `site-1`.

After the instance launches, one small change needs to be made in order to fix an issue with hostname binding. Assuming that the EC2 instance has a public domain name of `ec2-site-1.us-west-2.compute.amazonaws.com` and the SSL key is stored locally at  `~/.ssh/couchbase-key.pem`...

    local> ssh -i ~/.ssh/couchbase-key.pem ec2-user@ec2-site-1.us-west-2.compute.amazonaws.com
    ...
    couchbase> sudo nano /etc/hosts
    
and add the following line:

    127.0.0.1   ec2-site-1.us-west-2.compute.amazonaws.com

Next, browse to `http://ec2-site-1.us-west-2.compute.amazonaws.com:8091` to finish Couchbase setup. Lower the memory settings until they fit in your instance type. For hostname, use the instance's public DNS (`ec2-site-1.us-west-2.compute.amazonaws.com`), **do not** use `127.0.0.1` or `localhost`. Finish the setup with default values.

Return to `http://ec2-site-1.us-west-2.compute.amazonaws.com:8091` if not already there. (If asked for a username and password, the defaults are `Administrator` and `password`.) Create a new bucket named `assets` with a password of `password`.

Spring Data needs one additional view to do a `findAll` operation. Go to the `assets` bucket and create a view with the following properties:

* Design Document Name: `\_design/dev_assets`
* View Name: `all`
* Function:
```javascript
function (doc, meta) {
    if (doc._class == "org.trentbowman.purple_viper.domain.Asset") {
        emit(meta.id, null);
    }
}
```
* Reduce: `_count`

Then, publish the view.

#### XDCR

Repeat the steps above to start another Couchbase instance named `site-2`. Edit the Inbound whitelist of the security group (shared by both instances) to allow incoming requests from the internal addresses of both instances. This, along with the fixes to hostnames, should enable the stock instructions at https://developer.couchbase.com/documentation/server/4.5/xdcr/xdcr-create.html to "just work".

### REST Service

The REST frontend is a Spring Boot application using Spring Data to perform CRUD operations on Couchbase. A Java 8 SDK and Maven install are required.

Two instances are needed to simulate the two sites. Launch the first with

    SERVER_PORT=8080 COUCHBASE_CLUSTER_IP=ec2-site-1.us-west-2.compute.amazonaws.com mvn spring-boot:run

and the second with

    SERVER_PORT=8081 COUCHBASE_CLUSTER_IP=ec2-site-2.us-west-2.compute.amazonaws.com mvn spring-boot:run

Now go to `http://localhost:8080/assets` to see an index of all assets of site 1. (There aren't any yet.) Open another browser tab with `http://localhost:8081/assets` for site 2.

Create a new asset on site 1 with

    curl -X POST http://localhost:8080/assets -H "Content-Type: application/json" -d '{"uri":"asset://media/vhs", "name":"VHS Tapes"}'
    
Reload both asset indexes to see that the asset has been created on site 1 and replicated to site 2.
