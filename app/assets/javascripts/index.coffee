$ -> $.get "/members" , (members) -> $.each members , (index, member) ->  $("#list").append ("<tr> <td> <b>" + member.name + "</td> <td>" + member.balance  + "</td> </tr>")
