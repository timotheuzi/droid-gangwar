package com.example.droidgangwar.data

import com.example.droidgangwar.model.AlleywayRoom

object AlleywayRoomData {
    
    fun getAllRooms(): Map<String, AlleywayRoom> {
        return mapOf(
            "entrance" to AlleywayRoom(
                roomId = "entrance",
                title = "Dark Alley Entrance",
                description = "You stand at the foreboding entrance of a dark alleyway that cuts through the heart of the city like a jagged scar. The flickering streetlights above cast elongated, menacing shadows that dance across the cracked pavement, creating an atmosphere thick with tension and uncertainty. Distant sounds echo off the graffiti-stained brick walls - the muffled thump of bass from a nearby club, the occasional screech of tires, and the faint, unsettling laughter of unseen figures. The air is heavy with the acrid scent of urban decay, mixed with the sharp tang of garbage and something metallic, like old blood. Rats scurry in the darkness, their beady eyes reflecting the dim light as they watch you with primal suspicion. Every instinct screams that this is a place where the city's underbelly comes alive after dark, where deals are made in whispers and betrayals are settled with lead.",
                exits = mapOf(
                    "north" to "dead_end",
                    "south" to "city",
                    "east" to "side_street",
                    "west" to "dumpster"
                ),
                searchable = false,
                atmosphere = "foreboding"
            ),
            
            "dead_end" to AlleywayRoom(
                roomId = "dead_end",
                title = "Dead End",
                description = "You reach a claustrophobic dead end where the alley terminates abruptly against a towering brick wall, its surface a chaotic canvas of spray-painted graffiti that tells stories of gang rivalries, lost loves, and desperate cries for help. The wall looms over you like a silent sentinel, covered in layers of colorful but menacing artwork - crude drawings of guns, skulls with glowing eyes, and cryptic symbols that seem to pulse with hidden meaning. Trash accumulates in the corners like forgotten memories, piles of crumpled newspapers yellowed with age, discarded fast-food wrappers fluttering in the faint breeze, and the occasional hypodermic needle glinting dangerously in the shadows. The air is stagnant and oppressive, carrying the stench of urine and decay that clings to your clothes and burns your nostrils. There's an eerie silence here, broken only by the distant hum of traffic and the occasional drip of water from a leaking pipe overhead, creating an atmosphere of isolation and foreboding that makes your skin crawl.",
                exits = mapOf(
                    "south" to "entrance"
                ),
                searchable = true,
                searchableItems = listOf("money", "trash", "scattered_needles", "hidden_cash"),
                atmosphere = "claustrophobic"
            ),
            
            "side_street" to AlleywayRoom(
                roomId = "side_street",
                title = "Side Street",
                description = "You emerge onto a narrow side street that branches off from the main alley like a forgotten tributary, its cracked asphalt surface littered with potholes and stained with mysterious dark patches that could be oil or something far more sinister. The street is flanked by weathered brownstone buildings with fire escapes that hang precariously like rusted skeletons, their windows either boarded up or glowing with the dim, flickering light of cheap fluorescent bulbs. Cars occasionally crawl by with tinted windows and booming stereos, their drivers casting suspicious glances your way before accelerating into the night. Shady figures lurk in the doorways, their faces obscured by hoodies and shadows, watching your every move with predatory intensity. The air carries the mingled scents of cooking grease from a nearby takeout joint, cigarette smoke, and the unmistakable musk of danger that hangs over this forgotten corner of the city where the desperate and the dangerous converge under the indifferent gaze of the moon.",
                exits = mapOf(
                    "west" to "entrance",
                    "north" to "hidden_entrance",
                    "east" to "abandoned_lot"
                ),
                searchable = true,
                searchableItems = listOf("abandoned_items", "pocket_change", "lost_drugs"),
                atmosphere = "tense"
            ),
            
            "dumpster" to AlleywayRoom(
                roomId = "dumpster",
                title = "Behind the Dumpster",
                description = "You crouch behind a massive industrial dumpster that towers over you like a rusted behemoth, its dented metal sides covered in a patchwork of graffiti tags and mysterious symbols that seem to glow faintly in the dim light. The smell is overwhelming and nauseating - a toxic cocktail of rotting food, sour milk, chemical cleaners, and something darker, more primal, like the coppery tang of old blood mixed with the sharp bite of decay. As you huddle in the shadows, you notice discarded items scattered around your feet: crumpled cigarette packs, empty liquor bottles with labels worn away by time, a single high-heeled shoe caked in grime, and various pieces of trash that tell stories of lives lived on the edge. The ground beneath you is sticky and uneven, littered with broken glass that crunches softly underfoot and puddles of indeterminate liquid that reflect the distant streetlights like dark mirrors. Every sound is amplified in this confined space - the drip of condensation from the dumpster, the scuttle of unseen vermin, and the distant wail of sirens that seem to echo your growing sense of vulnerability.",
                exits = mapOf(
                    "east" to "entrance",
                    "north" to "burned_out_car"
                ),
                searchable = true,
                searchableItems = listOf("discarded_cash", "bottles", "hidden_weapon", "food_rations"),
                atmosphere = "concealed"
            ),
            
            "hidden_entrance" to AlleywayRoom(
                roomId = "hidden_entrance",
                title = "Hidden Entrance",
                description = "You discover a cleverly concealed entrance hidden behind a loose section of chain-link fence, partially obscured by overgrown weeds and strategically placed garbage bags that mask its presence from casual observers. The opening leads to what appears to be an underground network of tunnels and passages, their dark maw yawning like the mouth of some subterranean beast hungry for unwary explorers. Cool, damp air wafts up from below, carrying with it the earthy scent of wet soil, mildew, and something metallic that makes your skin prickle with anticipation. Faint sounds emanate from the depths - the drip of water echoing off unseen walls, the occasional skitter of small claws on concrete, and what might be the muffled murmur of voices or perhaps just the wind whistling through forgotten corridors. The entrance is framed by crumbling concrete edges, stained with mineral deposits that glitter like veins of ore in the dim light, hinting at the labyrinthine network that sprawls beneath the city streets, a hidden world where secrets are traded, alliances are forged, and dangers lurk in every shadow.",
                exits = mapOf(
                    "south" to "side_street",
                    "down" to "underground"
                ),
                searchable = true,
                searchableItems = listOf("hidden_stash", "tools", "underground_map"),
                atmosphere = "mysterious"
            ),
            
            "underground" to AlleywayRoom(
                roomId = "underground",
                title = "Underground Passage",
                description = "You descend a narrow, steeply sloping passage that leads deep beneath the city streets, the air growing cooler and heavier with each step as you leave the familiar world behind. The walls are rough-hewn concrete stained with mineral deposits that weep moisture like tears from the earth itself, and the ceiling arches overhead in a claustrophobic curve supported by rusted metal beams that groan ominously under their unseen burden. Water drips steadily from cracks in the ceiling, forming puddles on the uneven floor that reflect the dim glow of sporadic electric lights humming with barely contained energy. The echoes of your footsteps bounce off the walls in distorted waves, mingling with other sounds that seem to emanate from deeper within - the rumble of distant machinery, the faint hiss of steam escaping from unseen pipes, and what might be the shuffle of feet or the whisper of voices carried on currents of air you can't quite trace. The atmosphere is thick and oppressive, carrying the mingled scents of damp earth, corroded metal, and something acrid that burns the back of your throat, creating an environment where every shadow seems to conceal potential threats and every sound demands your heightened attention.",
                exits = mapOf(
                    "up" to "hidden_entrance",
                    "north" to "secret_room",
                    "east" to "flooded_chamber"
                ),
                searchable = true,
                searchableItems = listOf("waterproof_bag", "underground_equipment", "caches"),
                atmosphere = "claustrophobic"
            ),
            
            "secret_room" to AlleywayRoom(
                roomId = "secret_room",
                title = "Secret Room",
                description = "You step into a clandestine chamber hidden deep beneath the city, a space that feels both ancient and urgently alive with secrets waiting to be uncovered. The room is illuminated by the flickering glow of a single bare bulb hanging from a frayed cord, casting dramatic shadows across walls lined with metal shelves groaning under the weight of old wooden crates marked with faded stencils and mysterious symbols. Mysterious artifacts are scattered throughout - rusted tools that might have belonged to long-forgotten maintenance workers, yellowed documents bundled with twine, strange mechanical devices with exposed wiring and dials frozen in time, and containers that rattle softly when disturbed, hinting at contents both mundane and potentially valuable. The air is thick with dust motes that dance in the light beam, carrying the scent of aged paper, corroded metal, and something sweeter, almost cloying, that makes you wonder about the room's history. Cobwebs drape the corners like delicate lace curtains, and the floor is a mosaic of cracked tiles that crunch underfoot, each step echoing in the confined space and making you acutely aware that discovery could come at any moment.",
                exits = mapOf(
                    "south" to "underground"
                ),
                searchable = true,
                searchableItems = listOf("valuable_crates", "old_documents", "hidden_weapons", "rare_artifacts", "treasure_stash"),
                atmosphere = "secretive"
            ),
            
            "flooded_chamber" to AlleywayRoom(
                roomId = "flooded_chamber",
                title = "Flooded Chamber",
                description = "You wade into a subterranean chamber that has been partially flooded by some unknown disaster, the water lapping at your ankles with a cold, insistent pressure that speaks of deeper pools lurking just out of sight. The walls glisten with moisture, their concrete surfaces slick and treacherous, while the air hangs heavy with the mineral-rich scent of standing water mixed with the faint, unpleasant odor of mildew and decay. Strange objects float in the shallows - waterlogged newspapers with headlines from years past, bloated rats that bob gently with the current, and mysterious containers that might hold anything from forgotten tools to far more valuable contraband. The ceiling drips steadily, each drop creating expanding ripples on the water's surface that distort the dim light filtering down from above, and you can't shake the feeling that something watches you from the deeper shadows where the water grows darker and more impenetrable. Every step sends small waves radiating outward, the sound amplified in this enclosed space like whispers echoing through a tomb. Several of the floating containers look like they could be opened or dragged to shore, and you notice what appears to be a partially submerged metal box in the deeper water that might contain valuables if you're willing to risk getting soaked to retrieve it.",
                exits = mapOf(
                    "west" to "underground",
                    "north" to "ancient_vault"
                ),
                searchable = true,
                searchableItems = listOf("waterlogged_cash", "floating_containers", "submerged_box", "abandoned_supplies"),
                atmosphere = "eerie"
            ),
            
            "ancient_vault" to AlleywayRoom(
                roomId = "ancient_vault",
                title = "Ancient Vault",
                description = "You descend into what feels like a relic from the city's forgotten past - a vaulted chamber carved from the bedrock itself, its stone walls bearing the marks of tools that haven't been used in generations. The air is cool and dry, carrying the faint scent of aged stone and something metallic, almost like the breath of the earth itself exhaling after centuries of confinement. Massive stone pillars support the ceiling, each one carved with intricate reliefs depicting scenes from the city's history - immigrants arriving by ship, industrial barons building their empires, gangsters ruling the streets during prohibition. Dust motes dance in the dim light filtering through cracks in the ceiling, and the floor is worn smooth by countless footsteps that have trod this path over decades. Strange artifacts line the walls - rusted safes with their doors hanging open, yellowed ledgers with entries in faded ink, and containers that might hold anything from forgotten gold coins to far more dangerous secrets. The atmosphere is one of profound age and quiet power, as if this vault has witnessed the city's darkest moments and holds them close to its stony heart.",
                exits = mapOf(
                    "south" to "flooded_chamber"
                ),
                searchable = true,
                searchableItems = listOf("ancient_jewelry", "forgotten_artifacts", "gold_coins", "historical_documents"),
                atmosphere = "historic"
            ),
            
            "abandoned_lot" to AlleywayRoom(
                roomId = "abandoned_lot",
                title = "Abandoned Lot",
                description = "You step into a desolate abandoned lot overgrown with weeds and wild vegetation that has reclaimed this forgotten patch of urban wasteland, the cracked concrete foundation of what was once a building now serving as a canvas for nature's relentless invasion. Broken glass and rusted rebar litter the ground like the skeletal remains of some prehistoric beast, while the charred remains of old bonfires dot the landscape, their blackened circles containing the ghosts of countless nights spent by the homeless and desperate. The air carries the sharp, acrid scent of burned plastic mixed with the earthy aroma of wild plants pushing through the concrete, and distant traffic noise seems muffled and far away, as if this lot exists in its own isolated pocket of the city. Shadows play tricks here, with the overgrown vegetation creating natural hiding spots that could conceal anything from innocent squatters to far more dangerous predators waiting in ambush. You notice several suspicious mounds in the weeds that look like they might be hiding something valuable, and the foundation has cracks deep enough to conceal small items or even larger secrets buried beneath the surface.",
                exits = mapOf(
                    "west" to "side_street",
                    "north" to "construction_site"
                ),
                searchable = true,
                searchableItems = listOf("hidden_stash", "overgrown_caches", "buried_supplies", "secret_compartment"),
                atmosphere = "desolate"
            ),
            
            "burned_out_car" to AlleywayRoom(
                roomId = "burned_out_car",
                title = "Burned-Out Car",
                description = "You approach the blackened husk of what was once a sleek automobile, now reduced to a charred skeleton of twisted metal and melted plastic that stands as a grim monument to urban violence and desperation. The acrid stench of burned rubber and melted synthetic materials hangs heavy in the air, mixing with the faint, sickly sweet odor of accelerant that suggests this was no accidental fire. The windows have been blown out by the intense heat, leaving jagged glass teeth framing the darkness within, while the tires have melted into amorphous blobs of blackened rubber that pool around the wheel wells like congealed blood. Scorch marks radiate outward from the vehicle like dark veins across the pavement, telling a story of sudden, violent destruction that could have been caused by anything from a botched carjacking to a gang execution. The interior is a void of ash and debris, with only the faint outline of the driver's seat visible through the haze of destruction. Despite the devastation, you notice that the trunk compartment appears intact and the glove box hangs open invitingly, suggesting that valuables might still be hidden within the charred remains if you're brave enough to search through the dangerous wreckage.",
                exits = mapOf(
                    "south" to "dumpster",
                    "east" to "alley_graffiti_wall"
                ),
                searchable = true,
                searchableItems = listOf("charred_cash", "trunk_cache", "melted_jewelry", "burned_electronics"),
                atmosphere = "devastated"
            ),
            
            "construction_site" to AlleywayRoom(
                roomId = "construction_site",
                title = "Construction Site",
                description = "You find yourself in the midst of a long-abandoned construction site, a frozen snapshot of urban development gone wrong where steel girders reach skyward like the exposed ribs of some massive mechanical beast left to rust in the elements. Piles of construction debris - broken cinder blocks, tangled rebar, and discarded tools - create a treacherous landscape of potential hazards, while faded safety signs flutter weakly in the breeze, their warnings long since ignored by the ghosts of workers who abandoned this project years ago. The air carries the metallic tang of rust mixed with the damp earthiness of exposed soil, and the distant howl of wind through the skeletal framework creates an eerie symphony that makes your skin crawl. Deep trenches and uncovered manholes dot the ground like open wounds in the earth, each one a potential trap for the unwary explorer who ventures too far from the safety of the streetlights. Among the debris, you spot what looks like an old toolbox half-buried under rubble and several locked storage containers that might contain valuable construction equipment or hidden supplies if you can find a way to access them.",
                exits = mapOf(
                    "south" to "abandoned_lot",
                    "west" to "overgrown_garden"
                ),
                searchable = true,
                searchableItems = listOf("construction_tools", "locked_containers", "buried_supplies", "valuable_equipment"),
                atmosphere = "hazardous"
            ),
            
            "alley_graffiti_wall" to AlleywayRoom(
                roomId = "alley_graffiti_wall",
                title = "Graffiti Wall",
                description = "You stand before a massive brick wall that serves as a chaotic gallery of urban expression, its surface a living tapestry of spray-painted messages, symbols, and artwork that chronicles the hopes, hates, and histories of countless anonymous artists who have claimed this space as their canvas. Layer upon layer of paint creates a textured mosaic where older works peek through the cracks of newer ones, telling stories of gang territories marked and re-marked, lovers' declarations scrawled in hasty passion, and political rants that scream defiance against an uncaring world. The colors are vibrant and aggressive - electric blues and toxic greens, blood reds and midnight blacks - all competing for attention in this visual cacophony. The air smells of fresh paint mixed with the underlying decay of the alley, and you can't help but feel like you're intruding on a sacred space where the city's soul expresses itself in the most raw and unfiltered way possible.",
                exits = mapOf(
                    "west" to "burned_out_car",
                    "north" to "alley_fight_club",
                    "up" to "rooftop_access"
                ),
                searchable = false,
                atmosphere = "artistic"
            ),
            
            "alley_fight_club" to AlleywayRoom(
                roomId = "alley_fight_club",
                title = "Alley Fight Club",
                description = "You enter what appears to be an improvised fighting arena tucked away in the deepest recesses of the alley system, a brutal coliseum where the desperate and the damaged come to settle their differences with fists, feet, and whatever makeshift weapons they can scrounge from the surrounding decay. The ground is stained with old blood and scattered with discarded items - broken bottles, rusted chains, and strips of cloth that might have once been clothing - all bearing witness to the countless battles that have raged here under the cover of darkness. The air is thick with the metallic tang of blood mixed with the sour sweat of exertion, and the walls are scarred with impact marks and handprints smeared in crimson. Dim light filters through cracks in the surrounding buildings, casting everything in a reddish glow that makes the space feel like the antechamber to hell itself. You can almost hear the echoes of past fights - the crunch of bone on bone, the gasps of pain, the triumphant roars of victory - all hanging in the air like ghosts refusing to be laid to rest.",
                exits = mapOf(
                    "south" to "alley_graffiti_wall"
                ),
                searchable = false,
                atmosphere = "violent"
            ),
            
            "rooftop_access" to AlleywayRoom(
                roomId = "rooftop_access",
                title = "Rooftop Access",
                description = "You discover a precarious fire escape ladder leading up to the rooftops, its rusted rungs creaking ominously under your weight as you climb into a different world above the street level chaos. The rooftop expanse stretches out before you like a concrete desert under the night sky, dotted with ventilation shafts that hum with mechanical life, satellite dishes that track invisible signals, and the skeletal remains of old water towers standing like sentinels against the urban sprawl. The air up here is clearer, carrying the distant sounds of the city below while offering a panoramic view of the surrounding neighborhood - flickering neon signs, the glow of traffic lights, and the dark silhouettes of buildings that seem to lean in toward each other like conspirators. Clotheslines stretch between buildings like spider webs, heavy with laundry that flaps in the wind, while makeshift gardens in old barrels and buckets dot the landscape, tended by residents who have claimed these aerial territories as their own. The rooftop feels like a secret kingdom, where the rules of the street below don't apply and the view reminds you that there's always another level to the city's complex hierarchy.",
                exits = mapOf(
                    "down" to "alley_graffiti_wall",
                    "north" to "rooftop_garden",
                    "east" to "abandoned_roof_deck"
                ),
                searchable = true,
                searchableItems = listOf("rooftop_supplies", "maintenance_equipment", "hidden_stash"),
                atmosphere = "exposed"
            ),
            
            "rooftop_garden" to AlleywayRoom(
                roomId = "rooftop_garden",
                title = "Rooftop Garden Paradise",
                description = "You step into a breathtaking rooftop garden that transforms the harsh urban landscape into an unexpected oasis of greenery and tranquility, a hidden paradise cultivated by someone with vision and determination in the midst of concrete desolation. Lush vegetable gardens grow in carefully tended raised beds constructed from reclaimed materials, their leaves rustling softly in the breeze while tomatoes ripen on vines and herbs release their aromatic scents into the air. Colorful flowers bloom in mismatched pots and containers, creating a riot of color against the gray backdrop of the city, while comfortable seating areas made from weathered furniture offer places to rest and contemplate the view. The garden is a testament to human resilience, with solar panels providing power for string lights that twinkle like stars brought down to earth, and rain barrels collecting precious water for the thirsty plants. Butterflies and bees visit the blooms, their presence a reminder that life persists even in the most unlikely places. The air carries the fresh, earthy scent of growing things mixed with the faint aroma of cooking from nearby apartments, creating an atmosphere of peaceful productivity that stands in stark contrast to the chaos of the streets below.",
                exits = mapOf(
                    "south" to "rooftop_access",
                    "west" to "rooftop_hideout"
                ),
                searchable = true,
                searchableItems = listOf("fresh_produce", "garden_tools", "solar_supplies", "water_stores"),
                atmosphere = "peaceful"
            )
        )
    }
    
    fun getRoomById(roomId: String): AlleywayRoom? {
        return getAllRooms()[roomId]
    }
    
    fun getRandomRoom(): AlleywayRoom {
        val rooms = getAllRooms()
        val random = kotlin.random.Random(System.currentTimeMillis())
        return rooms.values.elementAt(random.nextInt(rooms.size))
    }
}
